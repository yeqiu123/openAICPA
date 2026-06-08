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
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.view.HapticFeedbackConstants;
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
    private static final String KEY_BEST_SCORE_PREFIX = "best_score_";
    private static final String KEY_COINS = "coins";
    private static final String KEY_STAR_CHEST_CLAIMED = "star_chest_claimed";
    private static final String KEY_CHAPTER_CHEST_PREFIX = "chapter_chest_";
    private static final String KEY_ACHIEVEMENT_PREFIX = "achievement_";
    private static final String KEY_WIN_STREAK = "win_streak";
    private static final String KEY_DAILY_REWARD_DAY = "daily_reward_day";
    private static final String KEY_DAILY_STREAK = "daily_streak";
    private static final String KEY_DAILY_CHALLENGE_DAY = "daily_challenge_day";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_HAPTIC_ENABLED = "haptic_enabled";
    private static final int BOARD_SIZE = 8;
    private static final int TILE_KINDS = 6;
    private static final int LEVEL_COUNT = 120;
    private static final int LEVELS_PER_PAGE = 60;
    private static final int CONTINUE_COST = 10;
    private static final int STAR_CHEST_STEP = 30;
    private static final int CHAPTER_SIZE = 20;
    private static final int CHAPTER_CHEST_STARS = 45;
    private static final int ACHIEVEMENT_COUNT = 6;
    private static final int NONE = -1;
    private static final int SPECIAL_NORMAL = 0;
    private static final int SPECIAL_ROW = 1;
    private static final int SPECIAL_COLUMN = 2;
    private static final int SPECIAL_RAINBOW = 3;
    private static final int SPECIAL_BOMB = 4;
    private static final int PROP_HAMMER = 0;
    private static final int PROP_BOMB = 1;
    private static final int PROP_SHUFFLE = 2;
    private static final int PROP_ROW_BLAST = 3;
    private static final int PROP_COLOR_BLAST = 4;
    private static final int PROP_EXTRA_MOVES = 5;
    private static final int PROP_MAGIC_WAND = 6;
    private static final int PROP_COUNT = 7;
    private static final int[] PROP_COSTS = {8, 12, 10, 16, 18, 14, 22};

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random(7);
    private final int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] ice = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] honey = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] stone = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] vine = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] gift = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] chain = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] keys = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] moveChest = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] shell = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[] propInventory = new int[PROP_COUNT];
    private final RectF[] propRects = new RectF[PROP_COUNT];
    private final RectF[] levelRects = new RectF[LEVELS_PER_PAGE];
    private final RectF mapButtonRect = new RectF();
    private final RectF hintButtonRect = new RectF();
    private final RectF settingsButtonRect = new RectF();
    private final RectF soundToggleRect = new RectF();
    private final RectF hapticToggleRect = new RectF();
    private final RectF prevPageRect = new RectF();
    private final RectF nextPageRect = new RectF();
    private final RectF starChestRect = new RectF();
    private final RectF dailyChallengeRect = new RectF();
    private final RectF chapterChestRect = new RectF();
    private final int[] levelStars = new int[LEVEL_COUNT];
    private final int[] levelBestScores = new int[LEVEL_COUNT];
    private final boolean[] chapterChestClaimed = new boolean[6];
    private final boolean[] achievementsClaimed = new boolean[ACHIEVEMENT_COUNT];
    private final List<Particle> particles = new ArrayList<>();
    private final List<Level> levels = new ArrayList<>();
    private final ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 45);
    private final int[] palette = {
            Color.rgb(255, 99, 132),
            Color.rgb(255, 205, 86),
            Color.rgb(54, 162, 235),
            Color.rgb(75, 192, 128),
            Color.rgb(153, 102, 255),
            Color.rgb(255, 159, 64)
    };
    private final int[] chapterTopColors = {
            Color.rgb(28, 177, 192),
            Color.rgb(102, 166, 255),
            Color.rgb(255, 142, 120),
            Color.rgb(120, 203, 142),
            Color.rgb(171, 132, 255),
            Color.rgb(255, 186, 82)
    };
    private final int[] chapterBottomColors = {
            Color.rgb(255, 151, 132),
            Color.rgb(255, 196, 112),
            Color.rgb(116, 219, 214),
            Color.rgb(255, 219, 106),
            Color.rgb(255, 139, 176),
            Color.rgb(92, 202, 166)
    };
    private final String[] chapterNames = {
            "糖果森林", "云朵海湾", "果冻火山", "薄荷花园", "星光梦境", "蜂蜜工坊"
    };

    private int levelIndex = 0;
    private int movesLeft;
    private int movesUsed;
    private int moveLimitBonus;
    private int score;
    private int targetKind;
    private int targetRemaining;
    private int iceRemaining;
    private int honeyRemaining;
    private int stoneRemaining;
    private int vineRemaining;
    private int chainRemaining;
    private int shellRemaining;
    private int keyRemaining;
    private int honeySpreadCount;
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
    private int levelMapPage;
    private int comboEnergy;
    private int bestCombo;
    private int lastStars;
    private int lastBonusScore;
    private boolean challengeCleared;
    private boolean comboChallengeCleared;
    private int coins;
    private int lastCoinReward;
    private int lastAchievementReward;
    private int winStreak;
    private int lastWinStreakReward;
    private int lastStarUpgradeReward;
    private int starChestClaimed;
    private int lastChestReward;
    private int lastChapterChestReward;
    private int lastGiftReward;
    private int lastMoveChestReward;
    private int dailyRewardAmount;
    private int dailyStreak;
    private int rewardTargetMilestone;
    private int rewardObstacleMilestone;
    private int rewardComboMilestone;
    private int rewardKeyMilestone;
    private int lastTaskRewardType;
    private long feedbackStartTime;
    private long hintUntilTime;
    private long chestNoticeUntilTime;
    private long levelIntroUntilTime;
    private float boardLeft;
    private float boardTop;
    private float tileSize;
    private boolean levelComplete;
    private boolean levelFailed;
    private boolean showingLevelMap;
    private boolean showingSettings;
    private boolean soundEnabled = true;
    private boolean hapticEnabled = true;
    private boolean dailyChallengeMode;
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
        if (showingSettings) {
            drawSettings(canvas);
            return;
        }
        drawHud(canvas);
        drawBoard(canvas);
        drawParticles(canvas);
        drawFeedback(canvas);
        drawLevelIntro(canvas);
        drawStatus(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }

        if (showingLevelMap) {
            handleLevelMapTap(event.getX(), event.getY());
            playHaptic(HapticFeedbackConstants.CLOCK_TICK);
            playClickTone();
            invalidate();
            return true;
        }

        if (showingSettings) {
            handleSettingsTap(event.getX(), event.getY());
            invalidate();
            return true;
        }

        if (levelComplete) {
            playHaptic(HapticFeedbackConstants.CONFIRM);
            playSuccessTone();
            if (dailyChallengeMode) {
                dailyChallengeMode = false;
                startLevel(highestUnlockedLevel);
            } else {
                startLevel((levelIndex + 1) % levels.size());
            }
            invalidate();
            return true;
        }

        if (levelFailed) {
            if (coins >= CONTINUE_COST) {
                // 金币续步给玩家一次翻盘机会。
                coins -= CONTINUE_COST;
                movesLeft = 5;
                moveLimitBonus += 5;
                levelFailed = false;
                saveCoins();
                playHaptic(HapticFeedbackConstants.CONFIRM);
                playSuccessTone();
            } else {
                startLevel(dailyChallengeMode ? highestUnlockedLevel : levelIndex);
                playHaptic(HapticFeedbackConstants.REJECT);
                playRejectTone();
            }
            invalidate();
            return true;
        }

        if (mapButtonRect.contains(event.getX(), event.getY())) {
            levelMapPage = levelIndex / LEVELS_PER_PAGE;
            showingLevelMap = true;
            playHaptic(HapticFeedbackConstants.CLOCK_TICK);
            playClickTone();
            invalidate();
            return true;
        }

        if (hintButtonRect.contains(event.getX(), event.getY())) {
            showAvailableHint();
            playHaptic(HapticFeedbackConstants.CLOCK_TICK);
            playClickTone();
            invalidate();
            return true;
        }

        if (settingsButtonRect.contains(event.getX(), event.getY())) {
            showingSettings = true;
            playHaptic(HapticFeedbackConstants.CLOCK_TICK);
            playClickTone();
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
        for (int i = 0; i < LEVEL_COUNT; i++) {
            int targetScore = 1200 + i * 260 + (i % 5) * 180;
            int moves = Math.max(14, 25 - i / 5);
            int hammer = 2 + (i % 3 == 0 ? 1 : 0);
            int bomb = 1 + (i % 4 == 0 ? 1 : 0);
            int shuffle = 1 + (i % 6 == 0 ? 1 : 0);
            int rowBlast = i >= 5 ? 1 + (i % 8 == 0 ? 1 : 0) : 0;
            int colorBlast = i >= 10 ? 1 + (i % 12 == 0 ? 1 : 0) : 0;
            int extraMoves = i >= 12 ? 1 + (i % 15 == 0 ? 1 : 0) : 0;
            int magicWand = i >= 20 && i % 14 == 0 ? 1 : 0;
            int targetKind = i % TILE_KINDS;
            int targetAmount = 8 + (i % 7) + i / 10;
            int iceCount = i < 4 ? i * 2 : Math.min(24, 6 + i / 2);
            int honeyCount = i < 8 ? 0 : Math.min(18, 4 + i / 4);
            int stoneCount = i < 15 ? 0 : Math.min(12, 3 + i / 8);
            int vineCount = i < 25 ? 0 : Math.min(16, 4 + i / 7);
            int giftCount = i < 12 ? 0 : Math.min(8, 2 + i / 18);
            int chainCount = i < 35 ? 0 : Math.min(14, 3 + i / 9);
            int shellCount = i < 45 ? 0 : Math.min(10, 2 + i / 12);
            int keyCount = i < 28 ? 0 : Math.min(6, 1 + i / 24 + (i % 9 == 0 ? 1 : 0));
            int moveChestCount = i >= 16 && i % 6 == 0 ? 1 + (i % 18 == 0 ? 1 : 0) : 0;
            int moveLimitGoal = i >= 18 && i % 4 == 0 ? Math.max(8, moves - 5) : 0;
            int comboGoal = i >= 22 && i % 5 == 0 ? 3 + (i / 25) : 0;
            levels.add(new Level(targetScore, moves, hammer, bomb, shuffle, rowBlast, colorBlast, extraMoves,
                    magicWand, targetKind, targetAmount, iceCount, honeyCount, stoneCount, vineCount, giftCount,
                    chainCount, shellCount, keyCount, moveChestCount, moveLimitGoal, comboGoal));
        }
    }

    private void startLevel(int index) {
        dailyChallengeMode = false;
        levelIndex = index;
        Level level = levels.get(levelIndex);
        movesLeft = level.moves;
        movesUsed = 0;
        moveLimitBonus = 0;
        score = 0;
        levelComplete = false;
        levelFailed = false;
        activeProp = NONE;
        comboEnergy = 0;
        bestCombo = 0;
        lastCoinReward = 0;
        lastAchievementReward = 0;
        lastWinStreakReward = 0;
        lastStarUpgradeReward = 0;
        lastChestReward = 0;
        lastChapterChestReward = 0;
        lastGiftReward = 0;
        lastMoveChestReward = 0;
        honeySpreadCount = 0;
        challengeCleared = false;
        comboChallengeCleared = false;
        rewardTargetMilestone = 0;
        rewardObstacleMilestone = 0;
        rewardComboMilestone = 0;
        rewardKeyMilestone = 0;
        lastTaskRewardType = 0;
        targetKind = level.targetKind;
        targetRemaining = level.targetAmount;
        iceRemaining = level.iceCount;
        honeyRemaining = level.honeyCount;
        stoneRemaining = level.stoneCount;
        vineRemaining = level.vineCount;
        chainRemaining = level.chainCount;
        shellRemaining = level.shellCount;
        keyRemaining = level.keyCount;
        honeySpreadCount = 0;
        propInventory[PROP_HAMMER] = level.hammers;
        propInventory[PROP_BOMB] = level.bombs;
        propInventory[PROP_SHUFFLE] = level.shuffles;
        propInventory[PROP_ROW_BLAST] = level.rowBlasts;
        propInventory[PROP_COLOR_BLAST] = level.colorBlasts;
        propInventory[PROP_EXTRA_MOVES] = level.extraMoves;
        propInventory[PROP_MAGIC_WAND] = level.magicWands;

        // 初始化时避开天然三连，让玩家第一步更清晰。
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ice[row][col] = 0;
                honey[row][col] = 0;
                stone[row][col] = 0;
                vine[row][col] = 0;
                gift[row][col] = 0;
                chain[row][col] = 0;
                keys[row][col] = 0;
                moveChest[row][col] = 0;
                shell[row][col] = 0;
                do {
                    board[row][col] = makePiece(random.nextInt(TILE_KINDS), SPECIAL_NORMAL);
                } while (createsInitialMatch(row, col));
            }
        }
        placeIce(level.iceCount);
        placeHoney(level.honeyCount);
        placeStone(level.stoneCount);
        placeVine(level.vineCount);
        placeGift(level.giftCount);
        placeChain(level.chainCount);
        placeShell(level.shellCount);
        placeKeys(level.keyCount);
        placeMoveChest(level.moveChestCount);
        ensurePlayableBoard();
        levelIntroUntilTime = System.currentTimeMillis() + 1400;
    }

    private void startDailyChallenge() {
        long today = getToday();
        int challengeIndex = (int) ((today * 37 + 11) % levels.size());
        startLevel(challengeIndex);
        // 每日挑战复用关卡池，但奖励和通关不推进主线。
        dailyChallengeMode = true;
        movesLeft = Math.max(12, movesLeft - 3);
        movesUsed = 0;
        score = 0;
    }

    private boolean handlePropTap(float x, float y) {
        for (int prop = 0; prop < PROP_COUNT; prop++) {
            RectF rect = propRects[prop];
            if (rect != null && rect.contains(x, y)) {
                if (propInventory[prop] <= 0) {
                    if (coins < PROP_COSTS[prop]) {
                        activeProp = NONE;
                        return true;
                    }
                    // 道具用完后可直接用金币补一个，减少关卡中断感。
                    coins -= PROP_COSTS[prop];
                    propInventory[prop]++;
                    saveCoins();
                }

                if (prop == PROP_SHUFFLE) {
                    propInventory[prop]--;
                    shuffleBoard();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_EXTRA_MOVES) {
                    // 加步道具即时生效，适合低步数时救场。
                    propInventory[prop]--;
                    movesLeft += 5;
                    moveLimitBonus += 5;
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else {
                    activeProp = activeProp == prop ? NONE : prop;
                    selectedRow = NONE;
                    selectedCol = NONE;
                }
                playHaptic(HapticFeedbackConstants.CLOCK_TICK);
                playClickTone();
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
        } else if (activeProp == PROP_MAGIC_WAND) {
            // 魔法棒把指定棋子升级成彩虹棋，让玩家主动创造关键大招。
            propInventory[PROP_MAGIC_WAND]--;
            board[row][col] = makePiece(colorOf(board[row][col]), SPECIAL_RAINBOW);
            spawnParticles(buildSingleCell(row, col));
            lastTaskRewardType = 5;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            showFeedback(1, 1);
        }
        playHaptic(HapticFeedbackConstants.CONFIRM);
        playSuccessTone();
        activeProp = NONE;
        selectedRow = NONE;
        selectedCol = NONE;
        checkLevelState();
    }

    private void trySwap(int row, int col) {
        int fromPiece = board[selectedRow][selectedCol];
        int toPiece = board[row][col];
        if (specialOf(fromPiece) != SPECIAL_NORMAL && specialOf(toPiece) != SPECIAL_NORMAL) {
            movesLeft--;
            movesUsed++;
            clearHint();
            clearCells(buildSpecialComboCells(selectedRow, selectedCol, row, col), 360);
            spreadHoneyAfterMove();
            checkLevelState();
            playHaptic(HapticFeedbackConstants.CONFIRM);
            playSuccessTone();
            selectedRow = NONE;
            selectedCol = NONE;
            return;
        }
        if (specialOf(fromPiece) != SPECIAL_NORMAL || specialOf(toPiece) != SPECIAL_NORMAL) {
            movesLeft--;
            movesUsed++;
            clearHint();
            // 特殊棋与普通棋互换时直接触发，减少误操作挫败感。
            Set<Cell> triggerCells = buildSpecialTriggerCells(selectedRow, selectedCol, row, col);
            if (specialOf(fromPiece) == SPECIAL_RAINBOW) {
                board[selectedRow][selectedCol] = makePiece(colorOf(fromPiece), SPECIAL_NORMAL);
            } else if (specialOf(toPiece) == SPECIAL_RAINBOW) {
                board[row][col] = makePiece(colorOf(toPiece), SPECIAL_NORMAL);
            }
            clearCells(triggerCells, 240);
            spreadHoneyAfterMove();
            checkLevelState();
            playHaptic(HapticFeedbackConstants.CONFIRM);
            playSuccessTone();
            selectedRow = NONE;
            selectedCol = NONE;
            return;
        }

        swap(selectedRow, selectedCol, row, col);
        Set<Cell> matches = findMatches();
        if (matches.isEmpty()) {
            swap(selectedRow, selectedCol, row, col);
            playHaptic(HapticFeedbackConstants.REJECT);
            playRejectTone();
        } else {
            movesLeft--;
            movesUsed++;
            clearHint();
            createSpecialFromMatch(matches, row, col);
            resolveMatches(matches);
            spreadHoneyAfterMove();
            checkLevelState();
            playHaptic(HapticFeedbackConstants.CONFIRM);
            playSuccessTone();
        }
        selectedRow = NONE;
        selectedCol = NONE;
    }

    private void clearCells(Set<Cell> cells, int bonusScore) {
        lastMoveChestReward = 0;
        cells = expandSpecialCells(cells);
        score += bonusScore + cells.size() * 45;
        spawnParticles(cells);
        removeCells(cells);
        grantTaskRewards();
        if (lastTaskRewardType == 0) {
            showNormalFeedback(1, cells.size());
        }
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

    private Set<Cell> buildSpecialComboCells(int rowA, int colA, int rowB, int colB) {
        int specialA = specialOf(board[rowA][colA]);
        int specialB = specialOf(board[rowB][colB]);
        Set<Cell> cells = new HashSet<>();

        // 两个特殊棋子互换时直接触发组合技，形成更爽快的大面积消除。
        if (specialA == SPECIAL_RAINBOW || specialB == SPECIAL_RAINBOW) {
            cells.addAll(buildColorCells(colorOf(board[rowA][colA])));
            cells.addAll(buildColorCells(colorOf(board[rowB][colB])));
        } else if (specialA == SPECIAL_BOMB || specialB == SPECIAL_BOMB) {
            cells.addAll(buildBombCells(rowA, colA));
            cells.addAll(buildBombCells(rowB, colB));
            cells.addAll(buildCrossCells(rowA, colA));
            cells.addAll(buildCrossCells(rowB, colB));
        } else {
            cells.addAll(buildCrossCells(rowA, colA));
            cells.addAll(buildCrossCells(rowB, colB));
        }
        cells.add(new Cell(rowA, colA));
        cells.add(new Cell(rowB, colB));
        return cells;
    }

    private Set<Cell> buildSpecialTriggerCells(int rowA, int colA, int rowB, int colB) {
        int specialA = specialOf(board[rowA][colA]);
        int specialRow = specialA == SPECIAL_NORMAL ? rowB : rowA;
        int specialCol = specialA == SPECIAL_NORMAL ? colB : colA;
        int targetRow = specialA == SPECIAL_NORMAL ? rowA : rowB;
        int targetCol = specialA == SPECIAL_NORMAL ? colA : colB;
        int special = specialOf(board[specialRow][specialCol]);
        Set<Cell> cells;

        if (special == SPECIAL_RAINBOW) {
            cells = buildColorCells(colorOf(board[targetRow][targetCol]));
        } else if (special == SPECIAL_ROW) {
            cells = new HashSet<>();
            for (int col = 0; col < BOARD_SIZE; col++) {
                cells.add(new Cell(specialRow, col));
            }
        } else if (special == SPECIAL_COLUMN) {
            cells = new HashSet<>();
            for (int row = 0; row < BOARD_SIZE; row++) {
                cells.add(new Cell(row, specialCol));
            }
        } else {
            cells = buildBombCells(specialRow, specialCol);
        }
        cells.add(new Cell(rowA, colA));
        cells.add(new Cell(rowB, colB));
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
        lastMoveChestReward = 0;
        while (!matches.isEmpty()) {
            combo++;
            matches = expandSpecialCells(matches);
            totalCleared += matches.size();
            score += matches.size() * 60 + (combo - 1) * 120;
            spawnParticles(matches);
            removeCells(matches);
            grantTaskRewards();
            collapseBoard();
            matches = findMatches();
        }
        ensurePlayableBoard();
        if (totalCleared > 0) {
            bestCombo = Math.max(bestCombo, combo);
            grantTaskRewards();
            comboEnergy = Math.min(100, comboEnergy + combo * 12 + totalCleared / 2);
            if (comboEnergy >= 100) {
                comboEnergy = 0;
                propInventory[random.nextInt(PROP_COUNT)]++;
                lastTaskRewardType = 3;
                lastGiftReward = 0;
                honeySpreadCount = 0;
                showFeedback(combo + 1, totalCleared);
            } else if (lastTaskRewardType == 0) {
                showNormalFeedback(combo, totalCleared);
            }
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
        if (levelComplete || levelFailed) {
            return;
        }

        Level level = levels.get(levelIndex);
        if (score >= level.targetScore && targetRemaining <= 0
                && iceRemaining <= 0 && honeyRemaining <= 0 && stoneRemaining <= 0 && vineRemaining <= 0
                && chainRemaining <= 0 && shellRemaining <= 0 && keyRemaining <= 0
                && isMoveLimitGoalCleared(level) && isComboGoalCleared(level)) {
            levelComplete = true;
            lastBonusScore = movesLeft * 80;
            score += lastBonusScore;
            spawnWinFireworks();
            lastStars = movesLeft > level.moves / 2 ? 3 : (movesLeft > level.moves / 5 ? 2 : 1);
            challengeCleared = level.moveLimitGoal > 0;
            comboChallengeCleared = level.comboGoal > 0;
            if (dailyChallengeMode) {
                saveDailyChallengeReward();
            } else {
                lastCoinReward = 10 + lastStars * 5;
                coins += lastCoinReward;
                grantWinStreakReward();
                saveLevelProgress();
            }
        } else if (movesLeft <= 0) {
            levelFailed = true;
            resetWinStreak();
        }
    }

    private boolean isMoveLimitGoalCleared(Level level) {
        return level.moveLimitGoal <= 0 || movesUsed <= getMoveLimitGoal(level);
    }

    private int getMoveLimitGoal(Level level) {
        return level.moveLimitGoal <= 0 ? 0 : level.moveLimitGoal + moveLimitBonus;
    }

    private boolean isComboGoalCleared(Level level) {
        return level.comboGoal <= 0 || bestCombo >= level.comboGoal;
    }

    private void saveDailyChallengeReward() {
        long today = getToday();
        if (prefs.getLong(KEY_DAILY_CHALLENGE_DAY, -1L) == today) {
            lastCoinReward = 0;
            return;
        }

        // 每日挑战独立奖励，不推进主线关卡进度。
        lastCoinReward = 30 + lastStars * 10;
        coins += lastCoinReward;
        prefs.edit()
                .putLong(KEY_DAILY_CHALLENGE_DAY, today)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void grantWinStreakReward() {
        winStreak++;
        lastWinStreakReward = winStreak >= 3 ? Math.min(60, winStreak * 5) : 0;
        coins += lastWinStreakReward;
        prefs.edit()
                .putInt(KEY_WIN_STREAK, winStreak)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void resetWinStreak() {
        if (dailyChallengeMode || winStreak <= 0) {
            return;
        }

        winStreak = 0;
        prefs.edit().putInt(KEY_WIN_STREAK, winStreak).apply();
    }

    private void grantAchievementRewards() {
        lastAchievementReward = 0;
        checkAchievement(0, getTotalStars() >= 30, 60);
        checkAchievement(1, highestUnlockedLevel >= 20, 80);
        checkAchievement(2, score >= 20000, 100);
        // 更多长期成就覆盖星级、连胜和章节进度，强化反复挑战动力。
        checkAchievement(3, getTotalStars() >= 120, 140);
        checkAchievement(4, winStreak >= 5, 120);
        checkAchievement(5, getFullyClearedChapterCount() >= 1, 160);
    }

    private void checkAchievement(int index, boolean reached, int reward) {
        if (achievementsClaimed[index] || !reached) {
            return;
        }

        // 成就奖励把长期目标转成可见金币反馈。
        achievementsClaimed[index] = true;
        lastAchievementReward += reward;
        coins += reward;
        prefs.edit()
                .putBoolean(KEY_ACHIEVEMENT_PREFIX + index, true)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void loadProgress() {
        highestUnlockedLevel = Math.min(prefs.getInt(KEY_UNLOCKED_LEVEL, 0), levels.size() - 1);
        coins = prefs.getInt(KEY_COINS, 30);
        starChestClaimed = prefs.getInt(KEY_STAR_CHEST_CLAIMED, 0);
        winStreak = prefs.getInt(KEY_WIN_STREAK, 0);
        soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true);
        hapticEnabled = prefs.getBoolean(KEY_HAPTIC_ENABLED, true);
        grantDailyReward();
        for (int i = 0; i < levels.size(); i++) {
            levelStars[i] = prefs.getInt(KEY_STARS_PREFIX + i, 0);
            levelBestScores[i] = prefs.getInt(KEY_BEST_SCORE_PREFIX + i, 0);
        }
        for (int i = 0; i < chapterChestClaimed.length; i++) {
            chapterChestClaimed[i] = prefs.getBoolean(KEY_CHAPTER_CHEST_PREFIX + i, false);
        }
        for (int i = 0; i < achievementsClaimed.length; i++) {
            achievementsClaimed[i] = prefs.getBoolean(KEY_ACHIEVEMENT_PREFIX + i, false);
        }
    }

    private void saveLevelProgress() {
        Level level = levels.get(levelIndex);
        int oldStars = levelStars[levelIndex];
        levelStars[levelIndex] = Math.max(levelStars[levelIndex], lastStars);
        levelBestScores[levelIndex] = Math.max(levelBestScores[levelIndex], score);
        highestUnlockedLevel = Math.max(highestUnlockedLevel, Math.min(levelIndex + 1, levels.size() - 1));
        if (levelStars[levelIndex] > oldStars) {
            // 重玩补星给额外金币，鼓励把老关卡刷到满星。
            lastStarUpgradeReward = (levelStars[levelIndex] - oldStars) * 12;
            coins += lastStarUpgradeReward;
        }
        grantAchievementRewards();
        prefs.edit()
                .putInt(KEY_UNLOCKED_LEVEL, highestUnlockedLevel)
                .putInt(KEY_STARS_PREFIX + levelIndex, levelStars[levelIndex])
                .putInt(KEY_BEST_SCORE_PREFIX + levelIndex, levelBestScores[levelIndex])
                .putInt(KEY_COINS, coins)
                .putInt(KEY_STAR_CHEST_CLAIMED, starChestClaimed)
                .apply();
    }

    private void saveCoins() {
        prefs.edit().putInt(KEY_COINS, coins).apply();
    }

    private void saveSettings() {
        prefs.edit()
                .putBoolean(KEY_SOUND_ENABLED, soundEnabled)
                .putBoolean(KEY_HAPTIC_ENABLED, hapticEnabled)
                .apply();
    }

    private void handleSettingsTap(float x, float y) {
        if (soundToggleRect.contains(x, y)) {
            soundEnabled = !soundEnabled;
            saveSettings();
            playHaptic(HapticFeedbackConstants.CLOCK_TICK);
            playClickTone();
            return;
        }
        if (hapticToggleRect.contains(x, y)) {
            hapticEnabled = !hapticEnabled;
            saveSettings();
            playHaptic(HapticFeedbackConstants.CLOCK_TICK);
            playClickTone();
            return;
        }
        showingSettings = false;
    }

    private void grantDailyReward() {
        long today = getToday();
        long lastRewardDay = prefs.getLong(KEY_DAILY_REWARD_DAY, -1L);
        dailyStreak = prefs.getInt(KEY_DAILY_STREAK, 0);
        if (lastRewardDay == today) {
            dailyRewardAmount = 0;
            return;
        }

        // 连续登录越久，金币补给越高，推动长期回访。
        dailyStreak = lastRewardDay == today - 1 ? dailyStreak + 1 : 1;
        dailyRewardAmount = 20 + Math.min(6, dailyStreak - 1) * 5;
        coins += dailyRewardAmount;
        prefs.edit()
                .putLong(KEY_DAILY_REWARD_DAY, today)
                .putInt(KEY_DAILY_STREAK, dailyStreak)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private long getToday() {
        return System.currentTimeMillis() / 86400000L;
    }

    private void handleLevelMapTap(float x, float y) {
        if (dailyChallengeRect.contains(x, y)) {
            showingLevelMap = false;
            startDailyChallenge();
            return;
        }

        if (chapterChestRect.contains(x, y)) {
            claimChapterChest();
            return;
        }

        if (starChestRect.contains(x, y)) {
            claimStarChest();
            return;
        }

        if (prevPageRect.contains(x, y) && levelMapPage > 0) {
            levelMapPage--;
            return;
        }
        if (nextPageRect.contains(x, y) && (levelMapPage + 1) * LEVELS_PER_PAGE < levels.size()) {
            levelMapPage++;
            return;
        }

        int pageStart = levelMapPage * LEVELS_PER_PAGE;
        for (int i = 0; i < LEVELS_PER_PAGE && pageStart + i < levels.size(); i++) {
            RectF rect = levelRects[i];
            int level = pageStart + i;
            if (rect != null && rect.contains(x, y) && level <= highestUnlockedLevel) {
                showingLevelMap = false;
                startLevel(level);
                return;
            }
        }
        showingLevelMap = false;
    }

    private void claimStarChest() {
        int available = getAvailableStarChests();
        if (available <= 0) {
            lastChestReward = 0;
            lastChapterChestReward = 0;
            chestNoticeUntilTime = System.currentTimeMillis() + 1400;
            return;
        }

        // 星级宝箱鼓励玩家反复挑战拿满星，并补充道具购买金币。
        starChestClaimed++;
        lastChestReward = 25 + starChestClaimed * 5;
        lastChapterChestReward = 0;
        coins += lastChestReward;
        prefs.edit()
                .putInt(KEY_STAR_CHEST_CLAIMED, starChestClaimed)
                .putInt(KEY_COINS, coins)
                .apply();
        chestNoticeUntilTime = System.currentTimeMillis() + 1800;
        playHaptic(HapticFeedbackConstants.CONFIRM);
        playSuccessTone();
    }

    private void claimChapterChest() {
        int chapter = getCurrentMapChapter();
        if (!canClaimChapterChest(chapter)) {
            lastChapterChestReward = 0;
            lastChestReward = 0;
            chestNoticeUntilTime = System.currentTimeMillis() + 1400;
            return;
        }

        // 章节宝箱奖励玩家把整章刷到高星，形成中期追求。
        chapterChestClaimed[chapter] = true;
        lastChapterChestReward = 80 + chapter * 20;
        lastChestReward = 0;
        coins += lastChapterChestReward;
        prefs.edit()
                .putBoolean(KEY_CHAPTER_CHEST_PREFIX + chapter, true)
                .putInt(KEY_COINS, coins)
                .apply();
        chestNoticeUntilTime = System.currentTimeMillis() + 1800;
        playHaptic(HapticFeedbackConstants.CONFIRM);
        playSuccessTone();
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

    private void placeVine(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (vine[row][col] == 0 && ice[row][col] == 0 && honey[row][col] == 0 && stone[row][col] == 0) {
                vine[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeGift(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (gift[row][col] == 0 && vine[row][col] == 0 && ice[row][col] == 0
                    && honey[row][col] == 0 && stone[row][col] == 0) {
                gift[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeChain(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (chain[row][col] == 0 && ice[row][col] == 0 && honey[row][col] == 0
                    && stone[row][col] == 0 && vine[row][col] == 0 && gift[row][col] == 0) {
                chain[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeShell(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (shell[row][col] == 0 && ice[row][col] == 0 && honey[row][col] == 0
                    && stone[row][col] == 0 && vine[row][col] == 0 && gift[row][col] == 0
                    && chain[row][col] == 0) {
                shell[row][col] = 2;
                placed++;
            }
        }
    }

    private void placeKeys(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (keys[row][col] == 0) {
                keys[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeMoveChest(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (moveChest[row][col] == 0 && gift[row][col] == 0 && stone[row][col] == 0 && vine[row][col] == 0) {
                moveChest[row][col] = 1;
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
            if (vine[cell.row][cell.col] > 0) {
                vine[cell.row][cell.col]--;
                vineRemaining--;
            }
            if (gift[cell.row][cell.col] > 0) {
                openGift();
                gift[cell.row][cell.col] = 0;
            }
            if (chain[cell.row][cell.col] > 0) {
                chain[cell.row][cell.col] = 0;
                chainRemaining--;
            }
            if (shell[cell.row][cell.col] > 0) {
                // 贝壳需要两次打击，制造更有节奏的中后期清障目标。
                shell[cell.row][cell.col]--;
                if (shell[cell.row][cell.col] == 0) {
                    shellRemaining--;
                }
            }
            if (keys[cell.row][cell.col] > 0) {
                // 钥匙是中后期收集目标，必须清到所在格才算拿到。
                keys[cell.row][cell.col] = 0;
                keyRemaining--;
            }
            if (moveChest[cell.row][cell.col] > 0) {
                // 步数宝箱提供翻盘空间，清到就返还少量步数。
                moveChest[cell.row][cell.col] = 0;
                movesLeft += 2;
                moveLimitBonus += 2;
                lastMoveChestReward += 2;
            }
            board[cell.row][cell.col] = NONE;
        }
    }

    private void openGift() {
        // 礼盒给随机惊喜，鼓励玩家顺手清理奖励格。
        if (random.nextBoolean()) {
            int reward = 5 + random.nextInt(8);
            coins += reward;
            lastGiftReward += reward;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            saveCoins();
        } else {
            propInventory[random.nextInt(PROP_COUNT)]++;
            lastGiftReward++;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
        }
    }

    private void spreadHoneyAfterMove() {
        Level level = levels.get(levelIndex);
        if (level.honeyCount <= 0 || movesUsed % 3 != 0 || honeyRemaining >= 24) {
            return;
        }

        List<Cell> candidates = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (honey[row][col] <= 0) {
                    continue;
                }
                collectHoneySpreadCells(row, col, candidates);
            }
        }
        if (candidates.isEmpty()) {
            return;
        }

        // 蜂蜜会定期蔓延，给玩家制造必须尽快清理的动态压力。
        Cell cell = candidates.get(random.nextInt(candidates.size()));
        honey[cell.row][cell.col] = 1;
        honeyRemaining++;
        honeySpreadCount++;
        lastGiftReward = 0;
        lastMoveChestReward = 0;
        showFeedback(1, honeySpreadCount);
    }

    private void collectHoneySpreadCells(int row, int col, List<Cell> candidates) {
        addHoneySpreadCell(row - 1, col, candidates);
        addHoneySpreadCell(row + 1, col, candidates);
        addHoneySpreadCell(row, col - 1, candidates);
        addHoneySpreadCell(row, col + 1, candidates);
    }

    private void addHoneySpreadCell(int row, int col, List<Cell> candidates) {
        if (isInside(row, col) && honey[row][col] == 0 && ice[row][col] == 0
                && stone[row][col] == 0 && vine[row][col] == 0 && gift[row][col] == 0) {
            candidates.add(new Cell(row, col));
        }
    }

    private void grantTaskRewards() {
        Level level = levels.get(levelIndex);
        int collected = level.targetAmount - targetRemaining;
        int targetMilestone = collected / 5;
        if (targetMilestone > rewardTargetMilestone) {
            // 局内阶段奖励，让收集目标也能持续反馈玩家。
            propInventory[PROP_HAMMER] += targetMilestone - rewardTargetMilestone;
            rewardTargetMilestone = targetMilestone;
            lastTaskRewardType = 1;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            showFeedback(1, 5);
        }

        int clearedObstacles = level.iceCount + level.honeyCount + level.stoneCount + level.vineCount + level.chainCount
                + level.shellCount - iceRemaining - honeyRemaining - stoneRemaining - vineRemaining - chainRemaining
                - shellRemaining;
        int obstacleMilestone = clearedObstacles / 6;
        if (obstacleMilestone > rewardObstacleMilestone) {
            // 清障越积极，道具补给越快。
            propInventory[PROP_BOMB] += obstacleMilestone - rewardObstacleMilestone;
            rewardObstacleMilestone = obstacleMilestone;
            lastTaskRewardType = 2;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            showFeedback(1, 6);
        }

        int comboMilestone = bestCombo / 3;
        if (comboMilestone > rewardComboMilestone) {
            // 做出大连击时给随机补给，奖励更有技巧性的消除。
            propInventory[random.nextInt(PROP_COUNT)] += comboMilestone - rewardComboMilestone;
            rewardComboMilestone = comboMilestone;
            lastTaskRewardType = 3;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            showFeedback(Math.max(2, bestCombo), 3);
        }

        if (level.keyCount > 0 && keyRemaining <= 0 && rewardKeyMilestone == 0) {
            // 收齐钥匙后补一个强力道具，让额外目标有即时爽感。
            propInventory[PROP_COLOR_BLAST]++;
            rewardKeyMilestone = 1;
            lastTaskRewardType = 4;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            showFeedback(1, level.keyCount);
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
        int special = matches.size() >= 6 ? SPECIAL_BOMB
                : (matches.size() >= 5 ? SPECIAL_RAINBOW : (selectedRow == row ? SPECIAL_ROW : SPECIAL_COLUMN));
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
                } else if (special == SPECIAL_BOMB) {
                    for (int nearRow = cell.row - 1; nearRow <= cell.row + 1; nearRow++) {
                        for (int nearCol = cell.col - 1; nearCol <= cell.col + 1; nearCol++) {
                            if (isInside(nearRow, nearCol)) {
                                changed |= expanded.add(new Cell(nearRow, nearCol));
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
        int chapter = getChapterIndex(levelIndex);
        paint.setShader(new LinearGradient(0, 0, 0, getHeight(),
                chapterTopColors[chapter], chapterBottomColors[chapter], Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setShader(null);

        paint.setColor(Color.argb(45, 255, 255, 255));
        canvas.drawCircle(getWidth() * 0.18f, getHeight() * 0.18f, getWidth() * 0.22f, paint);
        canvas.drawCircle(getWidth() * 0.82f, getHeight() * 0.76f, getWidth() * 0.28f, paint);
        long time = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            int alpha = 28 + (int) (18 * Math.abs(Math.sin(time / 650f + i)));
            paint.setColor(Color.argb(alpha, 255, 255, 255));
            float drift = (time / (35f + i * 3)) % Math.max(getWidth(), 1);
            float x = (i * 83 + drift) % Math.max(getWidth(), 1);
            float y = (i * 137 % Math.max(getHeight(), 1)) + dp(12);
            canvas.drawCircle(x, y, dp(2 + i % 3), paint);
        }
        postInvalidateOnAnimation();
    }

    private void drawHud(Canvas canvas) {
        Level level = levels.get(levelIndex);
        mapButtonRect.set(getWidth() - dp(90), dp(18), getWidth() - dp(22), dp(50));
        hintButtonRect.set(getWidth() - dp(164), dp(18), getWidth() - dp(96), dp(50));
        settingsButtonRect.set(getWidth() - dp(238), dp(18), getWidth() - dp(170), dp(50));

        paint.setColor(Color.argb(105, 255, 255, 255));
        canvas.drawRoundRect(settingsButtonRect, dp(14), dp(14), paint);
        canvas.drawRoundRect(hintButtonRect, dp(14), dp(14), paint);
        canvas.drawRoundRect(mapButtonRect, dp(14), dp(14), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(14));
        textPaint.setColor(Color.WHITE);
        canvas.drawText("设置", settingsButtonRect.centerX(), settingsButtonRect.centerY() + dp(5), textPaint);
        canvas.drawText("提示", hintButtonRect.centerX(), hintButtonRect.centerY() + dp(5), textPaint);
        canvas.drawText("选关", mapButtonRect.centerX(), mapButtonRect.centerY() + dp(5), textPaint);

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(sp(22));
        canvas.drawText(dailyChallengeMode ? "每日挑战" : "第 " + (levelIndex + 1) + " 关", dp(22), dp(48), textPaint);

        textPaint.setTextSize(sp(15));
        String modeLabel = dailyChallengeMode ? "今日特训" : chapterNames[getChapterIndex(levelIndex)];
        canvas.drawText(modeLabel + "  目标 " + level.targetScore, dp(22), dp(78), textPaint);
        canvas.drawText("分数 " + score, dp(22), dp(104), textPaint);
        canvas.drawText("收集 " + targetRemaining, dp(22), dp(130), textPaint);
        drawTargetSwatch(canvas, dp(96), dp(124));
        drawGoalProgress(canvas, level);

        textPaint.setTextAlign(Paint.Align.RIGHT);
        if (movesLeft <= 5 && !levelComplete && !levelFailed) {
            paint.setColor(Color.argb(145, 255, 88, 112));
            canvas.drawRoundRect(new RectF(getWidth() - dp(106), dp(58), getWidth() - dp(18), dp(86)),
                    dp(13), dp(13), paint);
        }
        canvas.drawText("步数 " + movesLeft, getWidth() - dp(22), dp(78), textPaint);
        String coinText = "金币 " + coins;
        if (dailyRewardAmount > 0) {
            coinText += " +" + dailyRewardAmount;
        }
        if (dailyStreak > 1) {
            coinText += " 连" + dailyStreak;
        }
        canvas.drawText(coinText, getWidth() - dp(22), dp(104), textPaint);
        String obstacleText = "冰" + iceRemaining + " 蜜" + honeyRemaining + " 石" + stoneRemaining
                + " 藤" + vineRemaining + " 锁" + chainRemaining;
        if (level.shellCount > 0) {
            obstacleText += " 贝" + shellRemaining;
        }
        if (level.keyCount > 0) {
            obstacleText += " 钥" + keyRemaining;
        }
        canvas.drawText(obstacleText, getWidth() - dp(22), dp(130), textPaint);
        textPaint.setTextSize(sp(13));
        String starText = buildStars(getPreviewStars(level));
        if (level.moveLimitGoal > 0) {
            starText += " 挑" + movesUsed + "/" + getMoveLimitGoal(level);
        }
        if (level.comboGoal > 0) {
            starText += " 连" + bestCombo + "/" + level.comboGoal;
        }
        canvas.drawText(starText, getWidth() - dp(22), dp(154), textPaint);
        drawComboEnergy(canvas);
    }

    private void drawBoard(Canvas canvas) {
        float availableWidth = getWidth() - dp(32);
        tileSize = availableWidth / BOARD_SIZE;
        boardLeft = dp(16);
        boardTop = Math.max(dp(164), (getHeight() - availableWidth) * 0.52f);

        if (movesLeft <= 5 && !levelComplete && !levelFailed) {
            paint.setColor(Color.argb(40, 255, 88, 112));
            canvas.drawRect(0, boardTop - dp(18), getWidth(), boardTop + tileSize * BOARD_SIZE + dp(18), paint);
        }

        paint.setColor(Color.argb(120, 255, 255, 255));
        RectF boardRect = new RectF(boardLeft - dp(8), boardTop - dp(8),
                boardLeft + tileSize * BOARD_SIZE + dp(8), boardTop + tileSize * BOARD_SIZE + dp(8));
        canvas.drawRoundRect(boardRect, dp(18), dp(18), paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        int chapterColor = chapterBottomColors[getChapterIndex(levelIndex)];
        paint.setColor(Color.argb(190, Color.red(chapterColor), Color.green(chapterColor), Color.blue(chapterColor)));
        canvas.drawRoundRect(boardRect, dp(18), dp(18), paint);
        paint.setStyle(Paint.Style.FILL);
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
        canvas.drawText("关卡地图 " + (levelMapPage + 1) + "/" + getLevelMapPageCount(), getWidth() / 2f, dp(54), textPaint);
        drawDailyChallengeEntry(canvas);
        drawChapterChestEntry(canvas);
        drawChapterProgress(canvas);

        int columns = 6;
        int pageStart = levelMapPage * LEVELS_PER_PAGE;
        int pageCount = Math.min(LEVELS_PER_PAGE, levels.size() - pageStart);
        int rows = (int) Math.ceil(pageCount / (float) columns);
        float gap = dp(6);
        float startY = dp(188);
        float pagerTop = getHeight() - dp(64);
        float sizeByWidth = (getWidth() - dp(32) - gap * (columns - 1)) / columns;
        float sizeByHeight = (pagerTop - startY - dp(10) - gap * (rows - 1)) / rows;
        float size = Math.min(sizeByWidth, sizeByHeight);
        float startX = (getWidth() - columns * size - (columns - 1) * gap) / 2f;

        for (int i = 0; i < LEVELS_PER_PAGE; i++) {
            levelRects[i] = null;
        }

        for (int i = 0; i < pageCount; i++) {
            int row = i / columns;
            int col = i % columns;
            int level = pageStart + i;
            float left = startX + col * (size + gap);
            float top = startY + row * (size + gap);
            RectF rect = new RectF(left, top, left + size, top + size);
            levelRects[i] = rect;

            boolean unlocked = level <= highestUnlockedLevel;
            int chapter = getChapterIndex(level);
            paint.setColor(unlocked ? Color.argb(185, Color.red(chapterBottomColors[chapter]),
                    Color.green(chapterBottomColors[chapter]), Color.blue(chapterBottomColors[chapter]))
                    : Color.argb(75, 33, 37, 56));
            canvas.drawRoundRect(rect, dp(10), dp(10), paint);

            if (level == levelIndex) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(3));
                paint.setColor(Color.WHITE);
                canvas.drawRoundRect(rect, dp(10), dp(10), paint);
                paint.setStyle(Paint.Style.FILL);
            }

            textPaint.setTextSize(sp(14));
            textPaint.setColor(unlocked ? Color.rgb(33, 37, 56) : Color.argb(145, 255, 255, 255));
            canvas.drawText(String.valueOf(level + 1), rect.centerX(), rect.centerY() - dp(1), textPaint);
            if (!unlocked) {
                drawMapLock(canvas, rect);
            }

            // 星级记录让关卡重玩有明确追求。
            if (levelStars[level] > 0) {
                textPaint.setTextSize(sp(10));
                canvas.drawText(buildStars(levelStars[level]), rect.centerX(), rect.bottom - dp(6), textPaint);
            }
            // 最佳分数给已通关关卡提供额外重玩目标。
            if (levelBestScores[level] > 0) {
                textPaint.setTextSize(sp(8));
                canvas.drawText("高" + levelBestScores[level], rect.centerX(), rect.top + dp(12), textPaint);
            }
        }

        drawLevelMapPager(canvas);
    }

    private void drawMapLock(Canvas canvas, RectF rect) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.argb(165, 255, 255, 255));
        float centerX = rect.centerX();
        float centerY = rect.centerY() + dp(12);
        canvas.drawRoundRect(new RectF(centerX - dp(8), centerY - dp(2),
                centerX + dp(8), centerY + dp(10)), dp(3), dp(3), paint);
        canvas.drawArc(new RectF(centerX - dp(7), centerY - dp(13),
                centerX + dp(7), centerY + dp(5)), 205, 130, false, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawDailyChallengeEntry(Canvas canvas) {
        dailyChallengeRect.set(dp(28), dp(70), getWidth() - dp(28), dp(104));
        boolean claimed = prefs.getLong(KEY_DAILY_CHALLENGE_DAY, -1L) == getToday();
        paint.setColor(claimed ? Color.argb(105, 255, 255, 255) : Color.argb(205, 255, 236, 133));
        canvas.drawRoundRect(dailyChallengeRect, dp(14), dp(14), paint);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(14));
        textPaint.setColor(claimed ? Color.WHITE : Color.rgb(33, 37, 56));
        String text = claimed ? "每日挑战 已领奖  再玩" : "每日挑战 今日奖励";
        canvas.drawText(text, dailyChallengeRect.centerX(), dailyChallengeRect.centerY() + dp(5), textPaint);
    }

    private void drawChapterChestEntry(Canvas canvas) {
        int chapter = getCurrentMapChapter();
        chapterChestRect.set(dp(28), dp(112), getWidth() - dp(28), dp(146));
        boolean claimable = canClaimChapterChest(chapter);
        paint.setColor(claimable ? Color.argb(205, 255, 236, 133) : Color.argb(105, 255, 255, 255));
        canvas.drawRoundRect(chapterChestRect, dp(14), dp(14), paint);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(14));
        textPaint.setColor(claimable ? Color.rgb(33, 37, 56) : Color.WHITE);
        String text = chapterChestClaimed[chapter] ? chapterNames[chapter] + " 已领"
                : chapterNames[chapter] + " 宝箱 " + getChapterStars(chapter) + "/" + CHAPTER_CHEST_STARS;
        canvas.drawText(text, chapterChestRect.centerX(), chapterChestRect.centerY() + dp(5), textPaint);
    }

    private void drawChapterProgress(Canvas canvas) {
        int chapter = getCurrentMapChapter();
        float left = dp(34);
        float top = dp(158);
        float right = getWidth() - dp(34);
        float progress = getChapterUnlockedCount(chapter) / (float) CHAPTER_SIZE;

        paint.setColor(Color.argb(85, 33, 37, 56));
        canvas.drawRoundRect(new RectF(left, top, right, top + dp(10)), dp(5), dp(5), paint);
        paint.setColor(Color.argb(220, 255, 236, 133));
        canvas.drawRoundRect(new RectF(left, top, left + (right - left) * progress, top + dp(10)), dp(5), dp(5), paint);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(12));
        textPaint.setColor(Color.WHITE);
        canvas.drawText("章节进度 " + getChapterUnlockedCount(chapter) + "/" + CHAPTER_SIZE
                + "  星 " + getChapterStars(chapter), getWidth() / 2f, top + dp(26), textPaint);
    }

    private void drawSettings(Canvas canvas) {
        paint.setColor(Color.argb(175, 33, 37, 56));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(26));
        textPaint.setColor(Color.WHITE);
        canvas.drawText("设置", getWidth() / 2f, getHeight() * 0.3f, textPaint);

        soundToggleRect.set(dp(54), getHeight() * 0.4f, getWidth() - dp(54), getHeight() * 0.4f + dp(48));
        hapticToggleRect.set(dp(54), getHeight() * 0.49f, getWidth() - dp(54), getHeight() * 0.49f + dp(48));
        drawSettingsToggle(canvas, soundToggleRect, "音效", soundEnabled);
        drawSettingsToggle(canvas, hapticToggleRect, "震动", hapticEnabled);

        textPaint.setTextSize(sp(14));
        canvas.drawText("点击空白返回", getWidth() / 2f, getHeight() * 0.62f, textPaint);
    }

    private void drawSettingsToggle(Canvas canvas, RectF rect, String label, boolean enabled) {
        paint.setColor(Color.argb(145, 255, 255, 255));
        canvas.drawRoundRect(rect, dp(16), dp(16), paint);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(sp(16));
        textPaint.setColor(Color.WHITE);
        canvas.drawText(label, rect.left + dp(18), rect.centerY() + dp(6), textPaint);

        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(enabled ? "开" : "关", rect.right - dp(18), rect.centerY() + dp(6), textPaint);
    }

    private void drawLevelMapPager(Canvas canvas) {
        float top = getHeight() - dp(64);
        prevPageRect.set(dp(24), top, dp(114), top + dp(38));
        nextPageRect.set(getWidth() - dp(114), top, getWidth() - dp(24), top + dp(38));
        starChestRect.set(getWidth() / 2f - dp(58), top, getWidth() / 2f + dp(58), top + dp(38));

        paint.setColor(levelMapPage > 0 ? Color.argb(150, 255, 255, 255) : Color.argb(55, 255, 255, 255));
        canvas.drawRoundRect(prevPageRect, dp(14), dp(14), paint);
        paint.setColor(getAvailableStarChests() > 0 ? Color.argb(205, 255, 236, 133) : Color.argb(105, 255, 255, 255));
        canvas.drawRoundRect(starChestRect, dp(14), dp(14), paint);
        paint.setColor((levelMapPage + 1) < getLevelMapPageCount()
                ? Color.argb(150, 255, 255, 255) : Color.argb(55, 255, 255, 255));
        canvas.drawRoundRect(nextPageRect, dp(14), dp(14), paint);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(14));
        textPaint.setColor(Color.WHITE);
        canvas.drawText("上一页", prevPageRect.centerX(), prevPageRect.centerY() + dp(5), textPaint);
        canvas.drawText("下一页", nextPageRect.centerX(), nextPageRect.centerY() + dp(5), textPaint);
        textPaint.setColor(Color.rgb(33, 37, 56));
        textPaint.setTextSize(sp(12));
        canvas.drawText(buildStarChestLabel(), starChestRect.centerX(), starChestRect.centerY() + dp(5), textPaint);
        drawStarChestNotice(canvas, top);
    }

    private int getLevelMapPageCount() {
        return (int) Math.ceil(levels.size() / (float) LEVELS_PER_PAGE);
    }

    private String buildStarChestLabel() {
        if (getAvailableStarChests() > 0) {
            return "宝箱+" + (25 + (starChestClaimed + 1) * 5);
        }
        return "星 " + getTotalStars() + "/" + getNextStarChestTarget();
    }

    private void drawStarChestNotice(Canvas canvas, float pagerTop) {
        if (System.currentTimeMillis() > chestNoticeUntilTime) {
            return;
        }

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(13));
        textPaint.setColor(Color.WHITE);
        String text = lastChestReward > 0 ? "星级宝箱 金币+" + lastChestReward
                : "还差 " + Math.max(0, getNextStarChestTarget() - getTotalStars()) + " 星";
        if (lastChapterChestReward > 0) {
            text = "章节宝箱 金币+" + lastChapterChestReward;
        }
        canvas.drawText(text, getWidth() / 2f, pagerTop - dp(10), textPaint);
        postInvalidateOnAnimation();
    }

    private int getAvailableStarChests() {
        return Math.max(0, getTotalStars() / STAR_CHEST_STEP - starChestClaimed);
    }

    private int getNextStarChestTarget() {
        return Math.min(LEVEL_COUNT * 3, (starChestClaimed + 1) * STAR_CHEST_STEP);
    }

    private int getTotalStars() {
        int total = 0;
        for (int i = 0; i < levels.size(); i++) {
            total += levelStars[i];
        }
        return total;
    }

    private boolean canClaimChapterChest(int chapter) {
        return !chapterChestClaimed[chapter] && getChapterStars(chapter) >= CHAPTER_CHEST_STARS;
    }

    private int getChapterStars(int chapter) {
        int total = 0;
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        for (int level = start; level < end; level++) {
            total += levelStars[level];
        }
        return total;
    }

    private int getFullyClearedChapterCount() {
        int count = 0;
        for (int chapter = 0; chapter < chapterNames.length; chapter++) {
            if (getChapterStars(chapter) >= CHAPTER_SIZE * 3) {
                count++;
            }
        }
        return count;
    }

    private int getChapterUnlockedCount(int chapter) {
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        int unlocked = 0;
        for (int level = start; level < end; level++) {
            if (level <= highestUnlockedLevel) {
                unlocked++;
            }
        }
        return unlocked;
    }

    private int getCurrentMapChapter() {
        int firstChapter = levelMapPage * LEVELS_PER_PAGE / CHAPTER_SIZE;
        int lastChapter = Math.min(chapterNames.length - 1,
                (levelMapPage * LEVELS_PER_PAGE + LEVELS_PER_PAGE - 1) / CHAPTER_SIZE);
        for (int chapter = firstChapter; chapter <= lastChapter; chapter++) {
            if (canClaimChapterChest(chapter)) {
                return chapter;
            }
        }
        for (int chapter = firstChapter; chapter <= lastChapter; chapter++) {
            if (!chapterChestClaimed[chapter]) {
                return chapter;
            }
        }
        return Math.min(chapterNames.length - 1, firstChapter);
    }

    private int getChapterIndex(int level) {
        return Math.min(chapterNames.length - 1, level / CHAPTER_SIZE);
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
        drawVine(canvas, row, col, rect);
        drawGift(canvas, row, col, rect);
        drawChain(canvas, row, col, rect);
        drawShell(canvas, row, col, rect);
        drawKey(canvas, row, col, rect);
        drawMoveChest(canvas, row, col, rect);
    }

    private void drawTargetSwatch(Canvas canvas, float centerX, float centerY) {
        paint.setColor(palette[targetKind]);
        canvas.drawCircle(centerX, centerY, dp(8), paint);
        paint.setColor(Color.argb(170, 255, 255, 255));
        canvas.drawCircle(centerX - dp(3), centerY - dp(3), dp(3), paint);
    }

    private void drawGoalProgress(Canvas canvas, Level level) {
        float left = dp(22);
        float top = dp(144);
        float width = getWidth() * 0.42f;
        float progress = Math.min(1f, score / (float) level.targetScore);

        paint.setColor(Color.argb(70, 33, 37, 56));
        RectF track = new RectF(left, top, left + width, top + dp(8));
        canvas.drawRoundRect(track, dp(4), dp(4), paint);

        paint.setColor(Color.argb(210, 255, 255, 255));
        RectF fill = new RectF(left, top, left + width * progress, top + dp(8));
        canvas.drawRoundRect(fill, dp(4), dp(4), paint);
    }

    private void drawComboEnergy(Canvas canvas) {
        float left = getWidth() - dp(164);
        float top = dp(144);
        float width = dp(142);
        float progress = comboEnergy / 100f;

        paint.setColor(Color.argb(70, 33, 37, 56));
        RectF track = new RectF(left, top, left + width, top + dp(8));
        canvas.drawRoundRect(track, dp(4), dp(4), paint);
        paint.setColor(Color.argb(210, 255, 236, 133));
        RectF fill = new RectF(left, top, left + width * progress, top + dp(8));
        canvas.drawRoundRect(fill, dp(4), dp(4), paint);
    }

    private int getPreviewStars(Level level) {
        if (movesLeft > level.moves / 2) {
            return 3;
        } else if (movesLeft > level.moves / 5) {
            return 2;
        }
        return 1;
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
            textPaint.setTextSize(sp(11));
            textPaint.setColor(Color.WHITE);
            String label = propInventory[prop] > 0
                    ? getPropName(prop) + " x" + propInventory[prop]
                    : getPropName(prop) + " " + PROP_COSTS[prop] + "币";
            canvas.drawText(label, rect.centerX(), rect.bottom - dp(10), textPaint);
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
        } else if (prop == PROP_COLOR_BLAST) {
            // 同色道具用圆环表示一键清掉同色棋子。
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, dp(14), paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centerX, centerY, dp(5), paint);
        } else if (prop == PROP_EXTRA_MOVES) {
            canvas.drawCircle(centerX, centerY, dp(14), paint);
            paint.setColor(Color.WHITE);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(sp(14));
            textPaint.setColor(Color.rgb(33, 37, 56));
            canvas.drawText("+5", centerX, centerY + dp(5), textPaint);
        } else {
            paint.setStrokeWidth(dp(4));
            canvas.drawLine(centerX - dp(12), centerY + dp(12), centerX + dp(10), centerY - dp(10), paint);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX + dp(12), centerY - dp(12), dp(5), paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centerX - dp(14), centerY + dp(14), dp(3), paint);
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
        } else if (prop == PROP_COLOR_BLAST) {
            return "同色";
        } else if (prop == PROP_MAGIC_WAND) {
            return "魔棒";
        }
        return "加步";
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
        } else if (special == SPECIAL_RAINBOW) {
            float pulse = 0.72f + 0.28f * (float) Math.sin(System.currentTimeMillis() / 150.0);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, tileSize * (0.23f + pulse * 0.05f), paint);
            paint.setStrokeWidth(dp(2));
            paint.setColor(Color.argb(170, 255, 236, 118));
            canvas.drawLine(centerX - tileSize * 0.2f, centerY, centerX + tileSize * 0.2f, centerY, paint);
            canvas.drawLine(centerX, centerY - tileSize * 0.2f, centerX, centerY + tileSize * 0.2f, paint);
            paint.setStyle(Paint.Style.FILL);
            postInvalidateOnAnimation();
        } else {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, tileSize * 0.23f, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centerX, centerY, tileSize * 0.08f, paint);
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

    private void drawVine(Canvas canvas, int row, int col, RectF rect) {
        if (vine[row][col] <= 0) {
            return;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(4));
        paint.setColor(Color.argb(190, 42, 132, 78));
        canvas.drawLine(rect.left + dp(8), rect.top + dp(10), rect.right - dp(8), rect.bottom - dp(10), paint);
        canvas.drawLine(rect.right - dp(10), rect.top + dp(9), rect.left + dp(10), rect.bottom - dp(9), paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(210, 120, 203, 142));
        canvas.drawCircle(rect.left + rect.width() * 0.28f, rect.top + rect.height() * 0.35f, dp(4), paint);
        canvas.drawCircle(rect.left + rect.width() * 0.72f, rect.top + rect.height() * 0.65f, dp(4), paint);
    }

    private void drawGift(Canvas canvas, int row, int col, RectF rect) {
        if (gift[row][col] <= 0) {
            return;
        }

        paint.setColor(Color.argb(190, 255, 236, 133));
        RectF box = new RectF(rect.left + dp(9), rect.top + dp(9), rect.right - dp(9), rect.bottom - dp(9));
        canvas.drawRoundRect(box, dp(6), dp(6), paint);
        paint.setColor(Color.argb(210, 255, 99, 132));
        canvas.drawRect(box.centerX() - dp(3), box.top, box.centerX() + dp(3), box.bottom, paint);
        canvas.drawRect(box.left, box.centerY() - dp(3), box.right, box.centerY() + dp(3), paint);
    }

    private void drawChain(Canvas canvas, int row, int col, RectF rect) {
        if (chain[row][col] <= 0) {
            return;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(4));
        paint.setColor(Color.argb(205, 245, 245, 255));
        canvas.drawLine(rect.left + dp(7), rect.top + dp(13), rect.right - dp(7), rect.bottom - dp(13), paint);
        canvas.drawLine(rect.left + dp(7), rect.bottom - dp(13), rect.right - dp(7), rect.top + dp(13), paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawShell(Canvas canvas, int row, int col, RectF rect) {
        if (shell[row][col] <= 0) {
            return;
        }

        paint.setColor(shell[row][col] > 1 ? Color.argb(180, 255, 245, 220) : Color.argb(140, 255, 245, 220));
        RectF shellRect = new RectF(rect.left + dp(8), rect.top + dp(10), rect.right - dp(8), rect.bottom - dp(10));
        canvas.drawOval(shellRect, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.argb(180, 255, 159, 180));
        for (int i = -1; i <= 1; i++) {
            canvas.drawLine(shellRect.centerX(), shellRect.bottom - dp(4),
                    shellRect.centerX() + i * dp(10), shellRect.top + dp(8), paint);
        }
        if (shell[row][col] == 1) {
            paint.setColor(Color.argb(210, 120, 92, 120));
            canvas.drawLine(shellRect.left + dp(9), shellRect.centerY(), shellRect.centerX(), shellRect.centerY() - dp(6), paint);
            canvas.drawLine(shellRect.centerX(), shellRect.centerY() - dp(6), shellRect.right - dp(10), shellRect.centerY() + dp(4), paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawKey(Canvas canvas, int row, int col, RectF rect) {
        if (keys[row][col] <= 0) {
            return;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        paint.setColor(Color.rgb(255, 236, 118));
        float centerX = rect.right - dp(14);
        float centerY = rect.top + dp(15);
        canvas.drawCircle(centerX - dp(6), centerY, dp(5), paint);
        canvas.drawLine(centerX - dp(1), centerY, centerX + dp(11), centerY, paint);
        canvas.drawLine(centerX + dp(6), centerY, centerX + dp(6), centerY + dp(5), paint);
        canvas.drawLine(centerX + dp(11), centerY, centerX + dp(11), centerY + dp(4), paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawMoveChest(Canvas canvas, int row, int col, RectF rect) {
        if (moveChest[row][col] <= 0) {
            return;
        }

        paint.setColor(Color.argb(205, 116, 219, 214));
        RectF box = new RectF(rect.left + dp(10), rect.bottom - dp(24), rect.right - dp(10), rect.bottom - dp(8));
        canvas.drawRoundRect(box, dp(5), dp(5), paint);
        paint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(12));
        textPaint.setColor(Color.rgb(33, 37, 56));
        canvas.drawText("+2", box.centerX(), box.centerY() + dp(4), textPaint);
        textPaint.setColor(Color.WHITE);
    }

    private void spawnParticles(Set<Cell> cells) {
        if (tileSize <= 0) {
            return;
        }

        for (Cell cell : cells) {
            int piece = board[cell.row][cell.col];
            int color = piece == NONE ? Color.WHITE : palette[colorOf(piece)];
            float centerX = boardLeft + cell.col * tileSize + tileSize / 2f;
            float centerY = boardTop + cell.row * tileSize + tileSize / 2f;
            for (int i = 0; i < 4; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                float speed = dp(1.8f + random.nextFloat() * 2.4f);
                particles.add(new Particle(centerX, centerY,
                        (float) Math.cos(angle) * speed,
                        (float) Math.sin(angle) * speed,
                        color, dp(3 + random.nextInt(4))));
            }
        }
    }

    private void spawnWinFireworks() {
        int count = Math.min(48, 16 + movesLeft * 2);
        for (int i = 0; i < count; i++) {
            float centerX = getWidth() / 2f + (random.nextFloat() - 0.5f) * getWidth() * 0.45f;
            float centerY = getHeight() * 0.38f + (random.nextFloat() - 0.5f) * dp(90);
            double angle = random.nextDouble() * Math.PI * 2;
            float speed = dp(2.2f + random.nextFloat() * 3.6f);
            int color = palette[random.nextInt(TILE_KINDS)];
            // 通关烟花把剩余步数奖励转成更明显的视觉反馈。
            particles.add(new Particle(centerX, centerY,
                    (float) Math.cos(angle) * speed,
                    (float) Math.sin(angle) * speed,
                    color, dp(4 + random.nextInt(5))));
        }
    }

    private void drawParticles(Canvas canvas) {
        long now = System.currentTimeMillis();
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            float age = (now - particle.birthTime) / 520f;
            if (age >= 1f) {
                particles.remove(i);
                continue;
            }
            int alpha = (int) (220 * (1f - age));
            paint.setColor(Color.argb(alpha, Color.red(particle.color), Color.green(particle.color), Color.blue(particle.color)));
            canvas.drawCircle(particle.x + particle.vx * age * 18f,
                    particle.y + particle.vy * age * 18f + dp(18) * age * age,
                    particle.size * (1f - age * 0.35f), paint);
        }
        if (!particles.isEmpty()) {
            postInvalidateOnAnimation();
        }
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
        if (lastGiftReward > 0 && age < 900) {
            text = "礼盒奖励 +" + lastGiftReward;
        } else if (lastMoveChestReward > 0 && age < 900) {
            text = "步数 +" + lastMoveChestReward;
        } else if (honeySpreadCount > 0 && age < 900) {
            text = "蜂蜜蔓延";
        } else if (lastTaskRewardType == 1 && age < 900) {
            text = "收集奖励";
        } else if (lastTaskRewardType == 2 && age < 900) {
            text = "清障奖励";
        } else if (lastTaskRewardType == 3 && age < 900) {
            text = "连击奖励";
        } else if (lastTaskRewardType == 4 && age < 900) {
            text = "钥匙奖励";
        } else if (lastTaskRewardType == 5 && age < 900) {
            text = "魔棒生成";
        }

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(feedbackCombo > 1 ? 24 : 19));
        textPaint.setColor(Color.argb(alpha, 255, 255, 255));
        paint.setColor(Color.argb(alpha / 2, 33, 37, 56));
        canvas.drawCircle(getWidth() / 2f, y - dp(8), dp(56 + feedbackCombo * 8), paint);
        canvas.drawText(text, getWidth() / 2f, y, textPaint);
        textPaint.setColor(Color.WHITE);
        postInvalidateOnAnimation();
    }

    private void drawLevelIntro(Canvas canvas) {
        long now = System.currentTimeMillis();
        if (now > levelIntroUntilTime || levelComplete || levelFailed) {
            return;
        }

        Level level = levels.get(levelIndex);
        float progress = (levelIntroUntilTime - now) / 1400f;
        int alpha = (int) (210 * Math.min(1f, progress + 0.25f));
        float centerY = getHeight() * 0.32f;
        paint.setColor(Color.argb(alpha, 33, 37, 56));
        canvas.drawRoundRect(new RectF(dp(34), centerY - dp(58), getWidth() - dp(34), centerY + dp(58)),
                dp(18), dp(18), paint);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(sp(22));
        String title = dailyChallengeMode ? "每日挑战" : "第 " + (levelIndex + 1) + " 关";
        canvas.drawText(title, getWidth() / 2f, centerY - dp(22), textPaint);
        textPaint.setTextSize(sp(15));
        String chapter = dailyChallengeMode ? "今日特训" : chapterNames[getChapterIndex(levelIndex)];
        canvas.drawText(chapter + "  目标分 " + level.targetScore, getWidth() / 2f, centerY + dp(6), textPaint);
        // 开场提示把本关重点目标先交代清楚。
        String goalText = "收集 " + level.targetAmount + "  清障 " + getLevelObstacleCount(level);
        if (level.keyCount > 0) {
            goalText += "  钥匙 " + level.keyCount;
        }
        if (level.moveChestCount > 0) {
            goalText += "  步箱 " + level.moveChestCount;
        }
        canvas.drawText(goalText,
                getWidth() / 2f, centerY + dp(32), textPaint);
        postInvalidateOnAnimation();
    }

    private int getLevelObstacleCount(Level level) {
        return level.iceCount + level.honeyCount + level.stoneCount + level.vineCount + level.chainCount
                + level.shellCount;
    }

    private void showFeedback(int combo, int cleared) {
        feedbackCombo = combo;
        feedbackCleared = cleared;
        feedbackStartTime = System.currentTimeMillis();
    }

    private void showNormalFeedback(int combo, int cleared) {
        lastTaskRewardType = 0;
        showFeedback(combo, cleared);
    }

    private void playClickTone() {
        if (!soundEnabled) {
            return;
        }
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 45);
    }

    private void playSuccessTone() {
        if (!soundEnabled) {
            return;
        }
        toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 80);
    }

    private void playRejectTone() {
        if (!soundEnabled) {
            return;
        }
        toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK, 90);
    }

    private void playHaptic(int feedbackConstant) {
        if (hapticEnabled) {
            performHapticFeedback(feedbackConstant);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        toneGenerator.release();
        super.onDetachedFromWindow();
    }

    private void drawStatus(Canvas canvas) {
        if (!levelComplete && !levelFailed) {
            return;
        }

        paint.setColor(Color.argb(185, 33, 37, 56));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setColor(levelComplete ? Color.argb(110, 255, 213, 92) : Color.argb(95, 255, 107, 154));
        canvas.drawCircle(getWidth() / 2f, getHeight() * 0.42f - dp(12), dp(92), paint);
        if (levelComplete && (lastAchievementReward > 0 || lastStarUpgradeReward > 0 || lastWinStreakReward > 0)) {
            drawRewardSparkles(canvas, getWidth() / 2f, getHeight() * 0.42f - dp(12));
        }

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(28));
        canvas.drawText(levelComplete ? "闯关成功" : "再试一次", getWidth() / 2f, getHeight() * 0.42f, textPaint);
        textPaint.setTextSize(sp(16));
        if (levelComplete) {
            String bonusText = buildStars(lastStars) + "  步数奖励 +" + lastBonusScore;
            if (challengeCleared) {
                bonusText += "  挑战达成";
            }
            if (comboChallengeCleared) {
                bonusText += "  连击达成";
            }
            canvas.drawText(bonusText, getWidth() / 2f, getHeight() * 0.49f, textPaint);
            String scoreText = dailyChallengeMode ? "挑战分 " + score : "最佳分 " + levelBestScores[levelIndex];
            canvas.drawText(scoreText, getWidth() / 2f, getHeight() * 0.55f, textPaint);
            String rewardText = "金币 +" + lastCoinReward + "  点击继续";
            if (dailyChallengeMode) {
                rewardText = lastCoinReward > 0 ? "每日金币 +" + lastCoinReward + "  返回主线" : "今日已领奖  返回主线";
            } else if (lastAchievementReward > 0) {
                rewardText = "金币 +" + lastCoinReward + "  成就奖励+" + lastAchievementReward + "  点击继续";
            } else if (lastStarUpgradeReward > 0) {
                rewardText = "金币 +" + lastCoinReward + "  补星+" + lastStarUpgradeReward + "  点击继续";
            } else if (lastWinStreakReward > 0) {
                rewardText = "金币 +" + lastCoinReward + " 连胜+" + lastWinStreakReward + "  点击继续";
            }
            canvas.drawText(rewardText, getWidth() / 2f, getHeight() * 0.61f, textPaint);
        } else if (coins >= CONTINUE_COST) {
            canvas.drawText("点击续步 -10金币", getWidth() / 2f, getHeight() * 0.49f, textPaint);
        } else if (dailyChallengeMode) {
            canvas.drawText("金币不足，返回主线", getWidth() / 2f, getHeight() * 0.49f, textPaint);
        } else {
            canvas.drawText("金币不足，点击重试", getWidth() / 2f, getHeight() * 0.49f, textPaint);
        }
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private void drawRewardSparkles(Canvas canvas, float centerX, float centerY) {
        paint.setColor(Color.argb(210, 255, 236, 118));
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI * 2 / 8 + System.currentTimeMillis() / 900.0;
            float x = centerX + (float) Math.cos(angle) * dp(76);
            float y = centerY + (float) Math.sin(angle) * dp(76);
            canvas.drawCircle(x, y, dp(i % 2 == 0 ? 4 : 3), paint);
        }
        postInvalidateOnAnimation();
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
        final int extraMoves;
        final int magicWands;
        final int targetKind;
        final int targetAmount;
        final int iceCount;
        final int honeyCount;
        final int stoneCount;
        final int vineCount;
        final int giftCount;
        final int chainCount;
        final int shellCount;
        final int keyCount;
        final int moveChestCount;
        final int moveLimitGoal;
        final int comboGoal;

        Level(int targetScore, int moves, int hammers, int bombs, int shuffles, int rowBlasts, int colorBlasts,
                int extraMoves, int magicWands,
                int targetKind, int targetAmount, int iceCount, int honeyCount, int stoneCount, int vineCount,
                int giftCount, int chainCount, int shellCount, int keyCount, int moveChestCount, int moveLimitGoal,
                int comboGoal) {
            this.targetScore = targetScore;
            this.moves = moves;
            this.hammers = hammers;
            this.bombs = bombs;
            this.shuffles = shuffles;
            this.rowBlasts = rowBlasts;
            this.colorBlasts = colorBlasts;
            this.extraMoves = extraMoves;
            this.magicWands = magicWands;
            this.targetKind = targetKind;
            this.targetAmount = targetAmount;
            this.iceCount = iceCount;
            this.honeyCount = honeyCount;
            this.stoneCount = stoneCount;
            this.vineCount = vineCount;
            this.giftCount = giftCount;
            this.chainCount = chainCount;
            this.shellCount = shellCount;
            this.keyCount = keyCount;
            this.moveChestCount = moveChestCount;
            this.moveLimitGoal = moveLimitGoal;
            this.comboGoal = comboGoal;
        }
    }

    private static class Particle {
        final float x;
        final float y;
        final float vx;
        final float vy;
        final int color;
        final float size;
        final long birthTime;

        Particle(float x, float y, float vx, float vy, int color, float size) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.size = size;
            this.birthTime = System.currentTimeMillis();
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
