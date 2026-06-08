package com.example.joymatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameView extends View {
    private static final String PREFS_NAME = "joy_match_progress";
    private static final String KEY_UNLOCKED_LEVEL = "unlocked_level";
    private static final String KEY_STARS_PREFIX = "stars_";
    private static final int BOARD_SIZE = 8;
    private static final int TILE_KINDS = 6;
    private static final int NONE = -1;
    private static final int SPECIAL_NORMAL = 0;
    private static final int SPECIAL_ROW = 1;
    private static final int SPECIAL_COLUMN = 2;
    private static final int SPECIAL_RAINBOW = 3;
    private static final int PROP_HAMMER = 0;
    private static final int PROP_BOMB = 1;
    private static final int PROP_SHUFFLE = 2;
    private static final int PROP_ROW_BLAST = 3;
    private static final int PROP_COLOR_BLAST = 4;
    private static final int PROP_COUNT = 5;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random(7);
    private final int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] ice = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] honey = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] stone = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[] propInventory = new int[PROP_COUNT];
    private final RectF[] propRects = new RectF[PROP_COUNT];
    private final RectF[] levelRects = new RectF[60];
    private final RectF mapButtonRect = new RectF();
    private final RectF hintButtonRect = new RectF();
    private final int[] levelStars = new int[60];
    private final List<Level> levels = new ArrayList<>();
    private final int[] palette = {
            Color.rgb(255, 99, 132),
            Color.rgb(255, 205, 86),
            Color.rgb(54, 162, 235),
            Color.rgb(75, 192, 128),
            Color.rgb(153, 102, 255),
            Color.rgb(255, 159, 64)
    };

    private int levelIndex = 0;
    private int movesLeft;
    private int score;
    private int targetKind;
    private int targetRemaining;
    private int iceRemaining;
    private int honeyRemaining;
    private int stoneRemaining;
    private int selectedRow = NONE;
    private int selectedCol = NONE;
    private int activeProp = NONE;
    private int hintRowA = NONE;
    private int hintColA = NONE;
    private int hintRowB = NONE;
    private int hintColB = NONE;
    private int feedbackCombo;
    private int feedbackCleared;
    private int highestUnlockedLevel;
    private int lastStars;
    private int lastBonusScore;
    private long feedbackStartTime;
    private long hintUntilTime;
    private float boardLeft;
    private float boardTop;
    private float tileSize;
    private boolean levelComplete;
    private boolean levelFailed;
    private boolean showingLevelMap;
    private SharedPreferences prefs;

    public GameView(Context context) {
        super(context);
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        paint.setStrokeCap(Paint.Cap.ROUND);
        setFocusable(true);
        buildLevels();
        loadProgress();
        startLevel(highestUnlockedLevel);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        if (showingLevelMap) {
            drawLevelMap(canvas);
            return;
        }
        drawHud(canvas);
        drawBoard(canvas);
        drawFeedback(canvas);
        drawStatus(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }

        if (showingLevelMap) {
            handleLevelMapTap(event.getX(), event.getY());
            invalidate();
            return true;
        }

        if (levelComplete || levelFailed) {
            startLevel(levelComplete ? (levelIndex + 1) % levels.size() : levelIndex);
            invalidate();
            return true;
        }

        if (mapButtonRect.contains(event.getX(), event.getY())) {
            showingLevelMap = true;
            invalidate();
            return true;
        }

        if (hintButtonRect.contains(event.getX(), event.getY())) {
            showAvailableHint();
            invalidate();
            return true;
        }

        if (handlePropTap(event.getX(), event.getY())) {
            invalidate();
            return true;
        }

        int col = (int) ((event.getX() - boardLeft) / tileSize);
        int row = (int) ((event.getY() - boardTop) / tileSize);
        if (!isInside(row, col)) {
            selectedRow = NONE;
            selectedCol = NONE;
            invalidate();
            return true;
        }

        if (activeProp != NONE) {
            useActiveProp(row, col);
            invalidate();
            return true;
        }

        if (selectedRow == NONE) {
            selectedRow = row;
            selectedCol = col;
        } else if (selectedRow == row && selectedCol == col) {
            selectedRow = NONE;
            selectedCol = NONE;
        } else if (Math.abs(selectedRow - row) + Math.abs(selectedCol - col) == 1) {
            trySwap(row, col);
        } else {
            selectedRow = row;
            selectedCol = col;
        }
        invalidate();
        return true;
    }

    private void buildLevels() {
        for (int i = 0; i < 60; i++) {
            int targetScore = 1200 + i * 260 + (i % 5) * 180;
            int moves = Math.max(14, 25 - i / 5);
            int hammer = 2 + (i % 3 == 0 ? 1 : 0);
            int bomb = 1 + (i % 4 == 0 ? 1 : 0);
            int shuffle = 1 + (i % 6 == 0 ? 1 : 0);
            int rowBlast = i >= 5 ? 1 + (i % 8 == 0 ? 1 : 0) : 0;
            int colorBlast = i >= 10 ? 1 + (i % 12 == 0 ? 1 : 0) : 0;
            int targetKind = i % TILE_KINDS;
            int targetAmount = 8 + (i % 7) + i / 10;
            int iceCount = i < 4 ? i * 2 : Math.min(24, 6 + i / 2);
            int honeyCount = i < 8 ? 0 : Math.min(18, 4 + i / 4);
            int stoneCount = i < 15 ? 0 : Math.min(12, 3 + i / 8);
            levels.add(new Level(targetScore, moves, hammer, bomb, shuffle, rowBlast, colorBlast,
                    targetKind, targetAmount, iceCount, honeyCount, stoneCount));
        }
    }

    private void startLevel(int index) {
        levelIndex = index;
        Level level = levels.get(levelIndex);
        movesLeft = level.moves;
        score = 0;
        levelComplete = false;
        levelFailed = false;
        activeProp = NONE;
        targetKind = level.targetKind;
        targetRemaining = level.targetAmount;
        iceRemaining = level.iceCount;
        honeyRemaining = level.honeyCount;
        stoneRemaining = level.stoneCount;
        propInventory[PROP_HAMMER] = level.hammers;
        propInventory[PROP_BOMB] = level.bombs;
        propInventory[PROP_SHUFFLE] = level.shuffles;
        propInventory[PROP_ROW_BLAST] = level.rowBlasts;
        propInventory[PROP_COLOR_BLAST] = level.colorBlasts;

        // 初始化时避开天然三连，让玩家第一步更清晰。
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ice[row][col] = 0;
                honey[row][col] = 0;
                stone[row][col] = 0;
                do {
                    board[row][col] = makePiece(random.nextInt(TILE_KINDS), SPECIAL_NORMAL);
                } while (createsInitialMatch(row, col));
            }
        }
        placeIce(level.iceCount);
        placeHoney(level.honeyCount);
        placeStone(level.stoneCount);
        ensurePlayableBoard();
    }

    private boolean handlePropTap(float x, float y) {
        for (int prop = 0; prop < PROP_COUNT; prop++) {
            RectF rect = propRects[prop];
            if (rect != null && rect.contains(x, y)) {
                if (propInventory[prop] <= 0) {
                    activeProp = NONE;
                } else if (prop == PROP_SHUFFLE) {
                    propInventory[prop]--;
                    shuffleBoard();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else {
                    activeProp = activeProp == prop ? NONE : prop;
                    selectedRow = NONE;
                    selectedCol = NONE;
                }
                return true;
            }
        }
        return false;
    }

    private void useActiveProp(int row, int col) {
        if (activeProp == PROP_HAMMER) {
            propInventory[PROP_HAMMER]--;
            clearCells(buildSingleCell(row, col), 90);
        } else if (activeProp == PROP_BOMB) {
            propInventory[PROP_BOMB]--;
            clearCells(buildBombCells(row, col), 140);
        } else if (activeProp == PROP_ROW_BLAST) {
            propInventory[PROP_ROW_BLAST]--;
            clearCells(buildCrossCells(row, col), 180);
        } else if (activeProp == PROP_COLOR_BLAST) {
            propInventory[PROP_COLOR_BLAST]--;
            clearCells(buildColorCells(colorOf(board[row][col])), 220);
        }
        activeProp = NONE;
        selectedRow = NONE;
        selectedCol = NONE;
        checkLevelState();
    }

    private void trySwap(int row, int col) {
        swap(selectedRow, selectedCol, row, col);
        Set<Cell> matches = findMatches();
        if (matches.isEmpty()) {
            swap(selectedRow, selectedCol, row, col);
        } else {
            movesLeft--;
            clearHint();
            createSpecialFromMatch(matches, row, col);
            resolveMatches(matches);
            checkLevelState();
        }
        selectedRow = NONE;
        selectedCol = NONE;
    }

    private void clearCells(Set<Cell> cells, int bonusScore) {
        cells = expandSpecialCells(cells);
        score += bonusScore + cells.size() * 45;
        removeCells(cells);
        showFeedback(1, cells.size());
        collapseBoard();
        resolveMatches(findMatches());
    }

    private Set<Cell> buildSingleCell(int row, int col) {
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(row, col));
        return cells;
    }

    private Set<Cell> buildBombCells(int row, int col) {
        Set<Cell> cells = new HashSet<>();
        for (int nearRow = row - 1; nearRow <= row + 1; nearRow++) {
            for (int nearCol = col - 1; nearCol <= col + 1; nearCol++) {
                if (isInside(nearRow, nearCol)) {
                    cells.add(new Cell(nearRow, nearCol));
                }
            }
        }
        return cells;
    }

    private Set<Cell> buildCrossCells(int row, int col) {
        Set<Cell> cells = new HashSet<>();
        for (int index = 0; index < BOARD_SIZE; index++) {
            cells.add(new Cell(row, index));
            cells.add(new Cell(index, col));
        }
        return cells;
    }

    private Set<Cell> buildColorCells(int color) {
        Set<Cell> cells = new HashSet<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (colorOf(board[row][col]) == color) {
                    cells.add(new Cell(row, col));
                }
            }
        }
        return cells;
    }

    private void shuffleBoard() {
        List<Integer> values = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                values.add(board[row][col]);
            }
        }
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int index = random.nextInt(values.size());
                board[row][col] = values.remove(index);
            }
        }
        resolveMatches(findMatches());
        ensurePlayableBoard();
        clearHint();
        checkLevelState();
    }

    private void resolveMatches(Set<Cell> matches) {
        int combo = 0;
        int totalCleared = 0;
        while (!matches.isEmpty()) {
            combo++;
            matches = expandSpecialCells(matches);
            totalCleared += matches.size();
            score += matches.size() * 60 + (combo - 1) * 120;
            removeCells(matches);
            collapseBoard();
            matches = findMatches();
        }
        ensurePlayableBoard();
        if (totalCleared > 0) {
            showFeedback(combo, totalCleared);
        }
    }

    private Set<Cell> findMatches() {
        Set<Cell> matches = new HashSet<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            int runStart = 0;
            for (int col = 1; col <= BOARD_SIZE; col++) {
                if (col < BOARD_SIZE && sameColorPiece(board[row][col], board[row][runStart])) {
                    continue;
                }
                if (board[row][runStart] != NONE && col - runStart >= 3) {
                    for (int matchCol = runStart; matchCol < col; matchCol++) {
                        matches.add(new Cell(row, matchCol));
                    }
                }
                runStart = col;
            }
        }

        for (int col = 0; col < BOARD_SIZE; col++) {
            int runStart = 0;
            for (int row = 1; row <= BOARD_SIZE; row++) {
                if (row < BOARD_SIZE && sameColorPiece(board[row][col], board[runStart][col])) {
                    continue;
                }
                if (board[runStart][col] != NONE && row - runStart >= 3) {
                    for (int matchRow = runStart; matchRow < row; matchRow++) {
                        matches.add(new Cell(matchRow, col));
                    }
                }
                runStart = row;
            }
        }
        return matches;
    }

    private void collapseBoard() {
        for (int col = 0; col < BOARD_SIZE; col++) {
            int writeRow = BOARD_SIZE - 1;
            for (int row = BOARD_SIZE - 1; row >= 0; row--) {
                if (board[row][col] != NONE) {
                    board[writeRow][col] = board[row][col];
                    writeRow--;
                }
            }
            while (writeRow >= 0) {
                board[writeRow][col] = makePiece(random.nextInt(TILE_KINDS), SPECIAL_NORMAL);
                writeRow--;
            }
        }
    }

    private void ensurePlayableBoard() {
        int attempts = 0;
        while ((!findMatches().isEmpty() || !hasAvailableMove()) && attempts < 8) {
            // 没有可走步时自动洗牌，避免玩家卡死在静态棋盘。
            shufflePiecesOnly();
            attempts++;
        }
    }

    private boolean hasAvailableMove() {
        return findAvailableMove() != null;
    }

    private Move findAvailableMove() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (col + 1 < BOARD_SIZE && wouldCreateMatch(row, col, row, col + 1)) {
                    return new Move(row, col, row, col + 1);
                }
                if (row + 1 < BOARD_SIZE && wouldCreateMatch(row, col, row + 1, col)) {
                    return new Move(row, col, row + 1, col);
                }
            }
        }
        return null;
    }

    private boolean wouldCreateMatch(int rowA, int colA, int rowB, int colB) {
        swap(rowA, colA, rowB, colB);
        boolean hasMatch = !findMatches().isEmpty();
        swap(rowA, colA, rowB, colB);
        return hasMatch;
    }

    private void shufflePiecesOnly() {
        List<Integer> values = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                values.add(board[row][col]);
            }
        }
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int index = random.nextInt(values.size());
                board[row][col] = values.remove(index);
            }
        }
    }

    private void showAvailableHint() {
        Move move = findAvailableMove();
        if (move == null) {
            ensurePlayableBoard();
            move = findAvailableMove();
        }
        if (move != null) {
            hintRowA = move.rowA;
            hintColA = move.colA;
            hintRowB = move.rowB;
            hintColB = move.colB;
            hintUntilTime = System.currentTimeMillis() + 1800;
        }
    }

    private void clearHint() {
        hintRowA = NONE;
        hintColA = NONE;
        hintRowB = NONE;
        hintColB = NONE;
        hintUntilTime = 0;
    }

    private void checkLevelState() {
        Level level = levels.get(levelIndex);
        if (score >= level.targetScore && targetRemaining <= 0
                && iceRemaining <= 0 && honeyRemaining <= 0 && stoneRemaining <= 0) {
            levelComplete = true;
            lastBonusScore = movesLeft * 80;
            score += lastBonusScore;
            saveLevelProgress();
        } else if (movesLeft <= 0) {
            levelFailed = true;
        }
    }

    private void loadProgress() {
        highestUnlockedLevel = Math.min(prefs.getInt(KEY_UNLOCKED_LEVEL, 0), levels.size() - 1);
        for (int i = 0; i < levels.size(); i++) {
            levelStars[i] = prefs.getInt(KEY_STARS_PREFIX + i, 0);
        }
    }

    private void saveLevelProgress() {
        Level level = levels.get(levelIndex);
        lastStars = movesLeft > level.moves / 2 ? 3 : (movesLeft > level.moves / 5 ? 2 : 1);
        if (lastStars <= levelStars[levelIndex] && highestUnlockedLevel >= Math.min(levelIndex + 1, levels.size() - 1)) {
            return;
        }

        levelStars[levelIndex] = Math.max(levelStars[levelIndex], lastStars);
        highestUnlockedLevel = Math.max(highestUnlockedLevel, Math.min(levelIndex + 1, levels.size() - 1));
        prefs.edit()
                .putInt(KEY_UNLOCKED_LEVEL, highestUnlockedLevel)
                .putInt(KEY_STARS_PREFIX + levelIndex, levelStars[levelIndex])
                .apply();
    }

    private void handleLevelMapTap(float x, float y) {
        for (int i = 0; i < levels.size(); i++) {
            RectF rect = levelRects[i];
            if (rect != null && rect.contains(x, y) && i <= highestUnlockedLevel) {
                showingLevelMap = false;
                startLevel(i);
                return;
            }
        }
        showingLevelMap = false;
    }

    private void placeIce(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (ice[row][col] == 0) {
                ice[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeHoney(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (honey[row][col] == 0 && ice[row][col] == 0) {
                honey[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeStone(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (stone[row][col] == 0 && ice[row][col] == 0 && honey[row][col] == 0) {
                stone[row][col] = 2;
                placed++;
            }
        }
    }

    private void removeCells(Set<Cell> cells) {
        for (Cell cell : cells) {
            int piece = board[cell.row][cell.col];
            if (piece != NONE && colorOf(piece) == targetKind && targetRemaining > 0) {
                targetRemaining--;
            }
            if (ice[cell.row][cell.col] > 0) {
                ice[cell.row][cell.col]--;
                iceRemaining--;
            }
            if (honey[cell.row][cell.col] > 0) {
                honey[cell.row][cell.col]--;
                honeyRemaining--;
            }
            if (stone[cell.row][cell.col] > 0) {
                stone[cell.row][cell.col]--;
                if (stone[cell.row][cell.col] == 0) {
                    stoneRemaining--;
                }
            }
            board[cell.row][cell.col] = NONE;
        }
    }

    private boolean createsInitialMatch(int row, int col) {
        int value = colorOf(board[row][col]);
        boolean horizontal = col >= 2 && colorOf(board[row][col - 1]) == value && colorOf(board[row][col - 2]) == value;
        boolean vertical = row >= 2 && colorOf(board[row - 1][col]) == value && colorOf(board[row - 2][col]) == value;
        return horizontal || vertical;
    }

    private void createSpecialFromMatch(Set<Cell> matches, int row, int col) {
        if (matches.size() < 4) {
            return;
        }

        Cell specialCell = matches.contains(new Cell(row, col)) ? new Cell(row, col) : matches.iterator().next();
        int special = matches.size() >= 5 ? SPECIAL_RAINBOW : (selectedRow == row ? SPECIAL_ROW : SPECIAL_COLUMN);
        board[specialCell.row][specialCell.col] = makePiece(colorOf(board[specialCell.row][specialCell.col]), special);
        matches.remove(specialCell);
    }

    private Set<Cell> expandSpecialCells(Set<Cell> origin) {
        Set<Cell> expanded = new HashSet<>(origin);
        boolean changed = true;
        while (changed) {
            changed = false;
            List<Cell> snapshot = new ArrayList<>(expanded);
            for (Cell cell : snapshot) {
                int piece = board[cell.row][cell.col];
                int special = specialOf(piece);
                if (special == SPECIAL_ROW) {
                    for (int col = 0; col < BOARD_SIZE; col++) {
                        changed |= expanded.add(new Cell(cell.row, col));
                    }
                } else if (special == SPECIAL_COLUMN) {
                    for (int row = 0; row < BOARD_SIZE; row++) {
                        changed |= expanded.add(new Cell(row, cell.col));
                    }
                } else if (special == SPECIAL_RAINBOW) {
                    int color = colorOf(piece);
                    for (int row = 0; row < BOARD_SIZE; row++) {
                        for (int col = 0; col < BOARD_SIZE; col++) {
                            if (colorOf(board[row][col]) == color) {
                                changed |= expanded.add(new Cell(row, col));
                            }
                        }
                    }
                }
            }
        }
        return expanded;
    }

    private int makePiece(int color, int special) {
        return color + special * TILE_KINDS;
    }

    private int colorOf(int piece) {
        return piece == NONE ? NONE : piece % TILE_KINDS;
    }

    private int specialOf(int piece) {
        return piece == NONE ? SPECIAL_NORMAL : piece / TILE_KINDS;
    }

    private boolean sameColorPiece(int first, int second) {
        return first != NONE && second != NONE && colorOf(first) == colorOf(second);
    }

    private void swap(int rowA, int colA, int rowB, int colB) {
        int temp = board[rowA][colA];
        board[rowA][colA] = board[rowB][colB];
        board[rowB][colB] = temp;
    }

    private boolean isInside(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private void drawBackground(Canvas canvas) {
        paint.setShader(new LinearGradient(0, 0, 0, getHeight(),
                Color.rgb(28, 177, 192), Color.rgb(255, 151, 132), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setShader(null);

        paint.setColor(Color.argb(45, 255, 255, 255));
        canvas.drawCircle(getWidth() * 0.18f, getHeight() * 0.18f, getWidth() * 0.22f, paint);
        canvas.drawCircle(getWidth() * 0.82f, getHeight() * 0.76f, getWidth() * 0.28f, paint);
        paint.setColor(Color.argb(36, 255, 255, 255));
        for (int i = 0; i < 12; i++) {
            float x = (i * 83 % Math.max(getWidth(), 1)) + dp(10);
            float y = (i * 137 % Math.max(getHeight(), 1)) + dp(12);
            canvas.drawCircle(x, y, dp(2 + i % 3), paint);
        }
    }

    private void drawHud(Canvas canvas) {
        Level level = levels.get(levelIndex);
        mapButtonRect.set(getWidth() - dp(90), dp(18), getWidth() - dp(22), dp(50));
        hintButtonRect.set(getWidth() - dp(164), dp(18), getWidth() - dp(96), dp(50));

        paint.setColor(Color.argb(105, 255, 255, 255));
        canvas.drawRoundRect(hintButtonRect, dp(14), dp(14), paint);
        canvas.drawRoundRect(mapButtonRect, dp(14), dp(14), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(14));
        textPaint.setColor(Color.WHITE);
        canvas.drawText("提示", hintButtonRect.centerX(), hintButtonRect.centerY() + dp(5), textPaint);
        canvas.drawText("选关", mapButtonRect.centerX(), mapButtonRect.centerY() + dp(5), textPaint);

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(sp(22));
        canvas.drawText("第 " + (levelIndex + 1) + " 关", dp(22), dp(48), textPaint);

        textPaint.setTextSize(sp(15));
        canvas.drawText("目标 " + level.targetScore, dp(22), dp(78), textPaint);
        canvas.drawText("分数 " + score, dp(22), dp(104), textPaint);
        canvas.drawText("收集 " + targetRemaining, dp(22), dp(130), textPaint);
        drawTargetSwatch(canvas, dp(96), dp(124));

        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("步数 " + movesLeft, getWidth() - dp(22), dp(78), textPaint);
        canvas.drawText("关卡 " + levels.size(), getWidth() - dp(22), dp(104), textPaint);
        canvas.drawText("冰" + iceRemaining + " 蜜" + honeyRemaining + " 石" + stoneRemaining,
                getWidth() - dp(22), dp(130), textPaint);
    }

    private void drawBoard(Canvas canvas) {
        float availableWidth = getWidth() - dp(32);
        tileSize = availableWidth / BOARD_SIZE;
        boardLeft = dp(16);
        boardTop = Math.max(dp(164), (getHeight() - availableWidth) * 0.52f);

        paint.setColor(Color.argb(120, 255, 255, 255));
        RectF boardRect = new RectF(boardLeft - dp(8), boardTop - dp(8),
                boardLeft + tileSize * BOARD_SIZE + dp(8), boardTop + tileSize * BOARD_SIZE + dp(8));
        canvas.drawRoundRect(boardRect, dp(18), dp(18), paint);
        paint.setColor(Color.argb(80, 33, 37, 56));
        for (int row = 0; row <= BOARD_SIZE; row++) {
            float y = boardTop + row * tileSize;
            canvas.drawLine(boardLeft, y, boardLeft + tileSize * BOARD_SIZE, y, paint);
        }
        for (int col = 0; col <= BOARD_SIZE; col++) {
            float x = boardLeft + col * tileSize;
            canvas.drawLine(x, boardTop, x, boardTop + tileSize * BOARD_SIZE, paint);
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                drawTile(canvas, row, col);
            }
        }
        drawPropBar(canvas);
    }

    private void drawLevelMap(Canvas canvas) {
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(sp(24));
        canvas.drawText("关卡地图", getWidth() / 2f, dp(54), textPaint);

        int columns = 6;
        int rows = (int) Math.ceil(levels.size() / (float) columns);
        float gap = dp(6);
        float sizeByWidth = (getWidth() - dp(32) - gap * (columns - 1)) / columns;
        float sizeByHeight = (getHeight() - dp(130) - gap * (rows - 1)) / rows;
        float size = Math.min(sizeByWidth, sizeByHeight);
        float startX = (getWidth() - columns * size - (columns - 1) * gap) / 2f;
        float startY = dp(82);

        for (int i = 0; i < levels.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            float left = startX + col * (size + gap);
            float top = startY + row * (size + gap);
            RectF rect = new RectF(left, top, left + size, top + size);
            levelRects[i] = rect;

            boolean unlocked = i <= highestUnlockedLevel;
            paint.setColor(unlocked ? Color.argb(170, 255, 255, 255) : Color.argb(75, 33, 37, 56));
            canvas.drawRoundRect(rect, dp(10), dp(10), paint);

            textPaint.setTextSize(sp(14));
            textPaint.setColor(unlocked ? Color.rgb(33, 37, 56) : Color.argb(145, 255, 255, 255));
            canvas.drawText(String.valueOf(i + 1), rect.centerX(), rect.centerY() - dp(1), textPaint);

            // 星级记录让关卡重玩有明确追求。
            if (levelStars[i] > 0) {
                textPaint.setTextSize(sp(10));
                canvas.drawText(buildStars(levelStars[i]), rect.centerX(), rect.bottom - dp(6), textPaint);
            }
        }
    }

    private String buildStars(int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append("*");
        }
        return builder.toString();
    }

    private void drawTile(Canvas canvas, int row, int col) {
        float left = boardLeft + col * tileSize + dp(4);
        float top = boardTop + row * tileSize + dp(4);
        float right = left + tileSize - dp(8);
        float bottom = top + tileSize - dp(8);
        float centerX = (left + right) / 2f;
        float centerY = (top + bottom) / 2f;
        int piece = board[row][col];
        int color = palette[colorOf(piece)];

        paint.setShader(new RadialGradient(centerX - tileSize * 0.18f, centerY - tileSize * 0.18f,
                tileSize * 0.58f, Color.WHITE, color, Shader.TileMode.CLAMP));
        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(rect, dp(14), dp(14), paint);
        paint.setShader(null);

        paint.setColor(Color.argb(70, 255, 255, 255));
        canvas.drawCircle(centerX - tileSize * 0.16f, centerY - tileSize * 0.18f, tileSize * 0.13f, paint);

        if (selectedRow == row && selectedCol == col) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(4));
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(rect, dp(14), dp(14), paint);
            paint.setStyle(Paint.Style.FILL);
        }

        if (isHintCell(row, col)) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(4));
            paint.setColor(Color.rgb(255, 244, 170));
            canvas.drawRoundRect(rect, dp(14), dp(14), paint);
            paint.setStyle(Paint.Style.FILL);
            postInvalidateOnAnimation();
        }

        drawTileIcon(canvas, colorOf(piece), centerX, centerY);
        drawSpecialMark(canvas, specialOf(piece), centerX, centerY);
        drawHoney(canvas, row, col, rect);
        drawIce(canvas, row, col, rect);
        drawStone(canvas, row, col, rect);
    }

    private void drawTargetSwatch(Canvas canvas, float centerX, float centerY) {
        paint.setColor(palette[targetKind]);
        canvas.drawCircle(centerX, centerY, dp(8), paint);
        paint.setColor(Color.argb(170, 255, 255, 255));
        canvas.drawCircle(centerX - dp(3), centerY - dp(3), dp(3), paint);
    }

    private boolean isHintCell(int row, int col) {
        if (System.currentTimeMillis() > hintUntilTime) {
            return false;
        }
        return (row == hintRowA && col == hintColA) || (row == hintRowB && col == hintColB);
    }

    private void drawPropBar(Canvas canvas) {
        float top = boardTop + tileSize * BOARD_SIZE + dp(18);
        float gap = dp(6);
        float buttonWidth = (getWidth() - dp(32) - gap * (PROP_COUNT - 1)) / PROP_COUNT;
        for (int prop = 0; prop < PROP_COUNT; prop++) {
            float left = dp(16) + prop * (buttonWidth + gap);
            RectF rect = new RectF(left, top, left + buttonWidth, top + dp(58));
            propRects[prop] = rect;

            paint.setColor(activeProp == prop ? Color.argb(235, 255, 255, 255) : Color.argb(120, 255, 255, 255));
            canvas.drawRoundRect(rect, dp(16), dp(16), paint);

            paint.setColor(Color.argb(propInventory[prop] > 0 ? 220 : 85, 33, 37, 56));
            drawPropIcon(canvas, prop, rect.centerX(), rect.centerY() - dp(7));

            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(sp(13));
            textPaint.setColor(Color.WHITE);
            canvas.drawText(getPropName(prop) + " x" + propInventory[prop], rect.centerX(), rect.bottom - dp(10), textPaint);
        }
    }

    private void drawPropIcon(Canvas canvas, int prop, float centerX, float centerY) {
        paint.setStrokeWidth(dp(4));
        if (prop == PROP_HAMMER) {
            canvas.drawRoundRect(new RectF(centerX - dp(12), centerY - dp(12), centerX + dp(12), centerY - dp(4)), dp(4), dp(4), paint);
            canvas.drawLine(centerX + dp(5), centerY, centerX + dp(16), centerY + dp(14), paint);
        } else if (prop == PROP_BOMB) {
            canvas.drawCircle(centerX, centerY + dp(2), dp(12), paint);
            canvas.drawLine(centerX + dp(6), centerY - dp(10), centerX + dp(13), centerY - dp(18), paint);
        } else if (prop == PROP_SHUFFLE) {
            Path path = new Path();
            path.moveTo(centerX - dp(15), centerY);
            path.lineTo(centerX - dp(2), centerY - dp(10));
            path.lineTo(centerX - dp(2), centerY - dp(3));
            path.lineTo(centerX + dp(15), centerY - dp(3));
            canvas.drawPath(path, paint);
            path.reset();
            path.moveTo(centerX + dp(15), centerY + dp(5));
            path.lineTo(centerX + dp(2), centerY + dp(15));
            path.lineTo(centerX + dp(2), centerY + dp(8));
            path.lineTo(centerX - dp(15), centerY + dp(8));
            canvas.drawPath(path, paint);
        } else if (prop == PROP_ROW_BLAST) {
            canvas.drawLine(centerX - dp(16), centerY, centerX + dp(16), centerY, paint);
            canvas.drawLine(centerX, centerY - dp(16), centerX, centerY + dp(16), paint);
        } else {
            // 同色道具用圆环表示一键清掉同色棋子。
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, dp(14), paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centerX, centerY, dp(5), paint);
        }
    }

    private String getPropName(int prop) {
        if (prop == PROP_HAMMER) {
            return "锤子";
        } else if (prop == PROP_BOMB) {
            return "炸弹";
        } else if (prop == PROP_SHUFFLE) {
            return "重排";
        } else if (prop == PROP_ROW_BLAST) {
            return "十字";
        }
        return "同色";
    }

    private void drawTileIcon(Canvas canvas, int kind, float centerX, float centerY) {
        paint.setColor(Color.argb(150, 255, 255, 255));
        float size = tileSize * 0.22f;
        if (kind == 0) {
            canvas.drawCircle(centerX, centerY, size, paint);
        } else if (kind == 1) {
            canvas.drawRect(centerX - size, centerY - size, centerX + size, centerY + size, paint);
        } else if (kind == 2) {
            Path path = new Path();
            path.moveTo(centerX, centerY - size);
            path.lineTo(centerX + size, centerY + size);
            path.lineTo(centerX - size, centerY + size);
            path.close();
            canvas.drawPath(path, paint);
        } else if (kind == 3) {
            canvas.drawOval(new RectF(centerX - size * 1.2f, centerY - size * 0.75f,
                    centerX + size * 1.2f, centerY + size * 0.75f), paint);
        } else if (kind == 4) {
            paint.setStrokeWidth(dp(4));
            canvas.drawLine(centerX - size, centerY - size, centerX + size, centerY + size, paint);
            canvas.drawLine(centerX + size, centerY - size, centerX - size, centerY + size, paint);
        } else {
            Path path = new Path();
            for (int i = 0; i < 5; i++) {
                double angle = -Math.PI / 2 + i * Math.PI * 2 / 5;
                float x = centerX + (float) Math.cos(angle) * size;
                float y = centerY + (float) Math.sin(angle) * size;
                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    private void drawSpecialMark(Canvas canvas, int special, float centerX, float centerY) {
        if (special == SPECIAL_NORMAL) {
            return;
        }

        paint.setColor(Color.argb(210, 255, 255, 255));
        paint.setStrokeWidth(dp(4));
        if (special == SPECIAL_ROW) {
            canvas.drawLine(centerX - tileSize * 0.28f, centerY, centerX + tileSize * 0.28f, centerY, paint);
        } else if (special == SPECIAL_COLUMN) {
            canvas.drawLine(centerX, centerY - tileSize * 0.28f, centerX, centerY + tileSize * 0.28f, paint);
        } else {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, tileSize * 0.27f, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    private void drawIce(Canvas canvas, int row, int col, RectF rect) {
        if (ice[row][col] <= 0) {
            return;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        paint.setColor(Color.argb(185, 210, 245, 255));
        canvas.drawRoundRect(rect, dp(14), dp(14), paint);
        canvas.drawLine(rect.left + rect.width() * 0.24f, rect.top + rect.height() * 0.18f,
                rect.left + rect.width() * 0.62f, rect.top + rect.height() * 0.58f, paint);
        canvas.drawLine(rect.left + rect.width() * 0.63f, rect.top + rect.height() * 0.58f,
                rect.left + rect.width() * 0.44f, rect.bottom - rect.height() * 0.16f, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawHoney(Canvas canvas, int row, int col, RectF rect) {
        if (honey[row][col] <= 0) {
            return;
        }

        paint.setColor(Color.argb(105, 255, 194, 64));
        canvas.drawRoundRect(rect, dp(14), dp(14), paint);
        paint.setColor(Color.argb(185, 255, 236, 133));
        canvas.drawCircle(rect.left + rect.width() * 0.3f, rect.top + rect.height() * 0.3f, dp(5), paint);
        canvas.drawCircle(rect.left + rect.width() * 0.68f, rect.top + rect.height() * 0.6f, dp(6), paint);
    }

    private void drawStone(Canvas canvas, int row, int col, RectF rect) {
        if (stone[row][col] <= 0) {
            return;
        }

        paint.setColor(Color.argb(150, 74, 80, 98));
        canvas.drawRoundRect(rect, dp(14), dp(14), paint);
        paint.setColor(Color.argb(140, 255, 255, 255));
        canvas.drawLine(rect.left + dp(10), rect.top + rect.height() * 0.35f,
                rect.right - dp(12), rect.top + rect.height() * 0.22f, paint);
        canvas.drawLine(rect.left + dp(13), rect.top + rect.height() * 0.67f,
                rect.right - dp(10), rect.top + rect.height() * 0.74f, paint);
    }

    private void drawFeedback(Canvas canvas) {
        long age = System.currentTimeMillis() - feedbackStartTime;
        if (feedbackCleared <= 0 || age > 1100) {
            return;
        }

        float progress = age / 1100f;
        int alpha = (int) (255 * (1f - progress));
        float y = boardTop - dp(18) - progress * dp(20);
        String text = feedbackCombo > 1 ? "连击 x" + feedbackCombo : "消除 +" + feedbackCleared;

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(feedbackCombo > 1 ? 24 : 19));
        textPaint.setColor(Color.argb(alpha, 255, 255, 255));
        paint.setColor(Color.argb(alpha / 2, 33, 37, 56));
        canvas.drawCircle(getWidth() / 2f, y - dp(8), dp(56 + feedbackCombo * 8), paint);
        canvas.drawText(text, getWidth() / 2f, y, textPaint);
        textPaint.setColor(Color.WHITE);
        postInvalidateOnAnimation();
    }

    private void showFeedback(int combo, int cleared) {
        feedbackCombo = combo;
        feedbackCleared = cleared;
        feedbackStartTime = System.currentTimeMillis();
    }

    private void drawStatus(Canvas canvas) {
        if (!levelComplete && !levelFailed) {
            return;
        }

        paint.setColor(Color.argb(185, 33, 37, 56));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setColor(levelComplete ? Color.argb(110, 255, 213, 92) : Color.argb(95, 255, 107, 154));
        canvas.drawCircle(getWidth() / 2f, getHeight() * 0.42f - dp(12), dp(92), paint);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(28));
        canvas.drawText(levelComplete ? "闯关成功" : "再试一次", getWidth() / 2f, getHeight() * 0.42f, textPaint);
        textPaint.setTextSize(sp(16));
        if (levelComplete) {
            canvas.drawText(buildStars(lastStars) + "  步数奖励 +" + lastBonusScore, getWidth() / 2f, getHeight() * 0.49f, textPaint);
            canvas.drawText("点击继续", getWidth() / 2f, getHeight() * 0.55f, textPaint);
        } else {
            canvas.drawText("点击继续", getWidth() / 2f, getHeight() * 0.49f, textPaint);
        }
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }

    private static class Level {
        final int targetScore;
        final int moves;
        final int hammers;
        final int bombs;
        final int shuffles;
        final int rowBlasts;
        final int colorBlasts;
        final int targetKind;
        final int targetAmount;
        final int iceCount;
        final int honeyCount;
        final int stoneCount;

        Level(int targetScore, int moves, int hammers, int bombs, int shuffles, int rowBlasts, int colorBlasts,
                int targetKind, int targetAmount, int iceCount, int honeyCount, int stoneCount) {
            this.targetScore = targetScore;
            this.moves = moves;
            this.hammers = hammers;
            this.bombs = bombs;
            this.shuffles = shuffles;
            this.rowBlasts = rowBlasts;
            this.colorBlasts = colorBlasts;
            this.targetKind = targetKind;
            this.targetAmount = targetAmount;
            this.iceCount = iceCount;
            this.honeyCount = honeyCount;
            this.stoneCount = stoneCount;
        }
    }

    private static class Cell {
        final int row;
        final int col;

        Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Cell)) {
                return false;
            }
            Cell other = (Cell) object;
            return row == other.row && col == other.col;
        }

        @Override
        public int hashCode() {
            return row * BOARD_SIZE + col;
        }
    }

    private static class Move {
        final int rowA;
        final int colA;
        final int rowB;
        final int colB;

        Move(int rowA, int colA, int rowB, int colB) {
            this.rowA = rowA;
            this.colA = colA;
            this.rowB = rowB;
            this.colB = colB;
        }
    }
}
