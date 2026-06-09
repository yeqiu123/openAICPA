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
    private static final String KEY_RANK_PREFIX = "rank_";
    private static final String KEY_HIDDEN_CHALLENGE_PREFIX = "hidden_challenge_";
    private static final String KEY_PERFECT_CLEAR_PREFIX = "perfect_clear_";
    private static final String KEY_COINS = "coins";
    private static final String KEY_PROP_RESERVE_PREFIX = "prop_reserve_";
    private static final String KEY_STAR_CHEST_CLAIMED = "star_chest_claimed";
    private static final String KEY_RANK_CHEST_CLAIMED = "rank_chest_claimed";
    private static final String KEY_CHAPTER_CHEST_PREFIX = "chapter_chest_";
    private static final String KEY_CHAPTER_MASTERY_PREFIX = "chapter_mastery_";
    private static final String KEY_CHAPTER_ELITE_PREFIX = "chapter_elite_";
    private static final String KEY_CHAPTER_RANK_PREFIX = "chapter_rank_";
    private static final String KEY_CHAPTER_HIDDEN_PREFIX = "chapter_hidden_";
    private static final String KEY_CHAPTER_PERFECT_PREFIX = "chapter_perfect_";
    private static final String KEY_ACHIEVEMENT_PREFIX = "achievement_";
    private static final String KEY_FAIL_STREAK_PREFIX = "fail_streak_";
    private static final String KEY_WIN_STREAK = "win_streak";
    private static final String KEY_DAILY_REWARD_DAY = "daily_reward_day";
    private static final String KEY_DAILY_STREAK = "daily_streak";
    private static final String KEY_DAILY_CHALLENGE_DAY = "daily_challenge_day";
    private static final String KEY_DAILY_CHALLENGE_STREAK = "daily_challenge_streak";
    private static final String KEY_DAILY_GOAL_DAY = "daily_goal_day";
    private static final String KEY_DAILY_GOAL_PROGRESS = "daily_goal_progress";
    private static final String KEY_DAILY_GOAL_CLAIMED = "daily_goal_claimed";
    private static final String KEY_SEASON_LEVELS = "season_levels";
    private static final String KEY_SEASON_STARS = "season_stars";
    private static final String KEY_SEASON_REWARD_STEP = "season_reward_step";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_HAPTIC_ENABLED = "haptic_enabled";
    private static final int BOARD_SIZE = 8;
    private static final int TILE_KINDS = 6;
    private static final int LEVEL_COUNT = 400;
    private static final int LEVELS_PER_PAGE = 60;
    private static final int CONTINUE_COST = 10;
    private static final int STAR_CHEST_STEP = 30;
    private static final int RANK_CHEST_STEP = 45;
    private static final int CHAPTER_SIZE = 20;
    private static final int CHAPTER_COUNT = 20;
    private static final int CHAPTER_CHEST_STARS = 45;
    private static final int ACHIEVEMENT_COUNT = 40;
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
    private static final int PROP_BRUSH = 7;
    private static final int PROP_PORTAL = 8;
    private static final int PROP_CLEANSE = 9;
    private static final int PROP_FREEZE = 10;
    private static final int PROP_MAGNET = 11;
    private static final int PROP_CLOCK = 12;
    private static final int PROP_STAR_HAMMER = 13;
    private static final int PROP_ROCKET = 14;
    private static final int PROP_TARGET_BRUSH = 15;
    private static final int PROP_SHIELD = 16;
    private static final int PROP_ENERGY_CORE = 17;
    private static final int PROP_CHAIN_BREAKER = 18;
    private static final int PROP_LIGHTNING = 19;
    private static final int PROP_METEOR = 20;
    private static final int PROP_TIDE = 21;
    private static final int PROP_AURORA_ORB = 22;
    private static final int PROP_STARFISH_PICK = 23;
    private static final int PROP_MOON_TICKET = 24;
    private static final int PROP_FIREWORK_CANNON = 25;
    private static final int PROP_STAR_COMPASS = 26;
    private static final int PROP_BUBBLE_WAND = 27;
    private static final int PROP_SNOW_GLOBE = 28;
    private static final int PROP_STAR_HARP = 29;
    private static final int PROP_COUNT = 30;
    private static final int[] PROP_COSTS = {8, 12, 10, 16, 18, 14, 22, 20, 24, 20, 18, 16, 18, 26, 18, 20, 22, 24, 20, 22, 24, 26, 28, 24, 26, 30, 32, 34, 36, 38};
    private static final int[] CHAPTER_CHEST_PROPS = {
            PROP_CLEANSE, PROP_TIDE, PROP_METEOR, PROP_BUBBLE_WAND, PROP_STAR_COMPASS,
            PROP_FREEZE, PROP_STARFISH_PICK, PROP_AURORA_ORB, PROP_MOON_TICKET, PROP_FIREWORK_CANNON,
            PROP_AURORA_ORB, PROP_STAR_COMPASS, PROP_BUBBLE_WAND, PROP_FIREWORK_CANNON, PROP_SNOW_GLOBE,
            PROP_STAR_COMPASS, PROP_CLOCK, PROP_AURORA_ORB, PROP_STAR_HARP, PROP_STAR_HARP
    };
    private static final int[] CHAPTER_CHEST_PROP_AMOUNTS = {
            1, 1, 1, 1, 1,
            1, 1, 1, 1, 1,
            1, 1, 1, 1, 2,
            1, 1, 1, 2, 2
    };

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
    private final int[][] coralReef = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] cloud = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] flower = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] gem = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] goldenEgg = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] coinPouch = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] paintBucket = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] windmill = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] jewelBow = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] stardustJar = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] wishLamp = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] resonanceDrum = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] auroraPrism = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] rainbowBottle = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] energyPotion = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] butterfly = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] portal = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] hourglass = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] luckyStar = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] luckyClover = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] mysteryBox = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] countdownBomb = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] pearl = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] carousel = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] ferrisTicket = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] fireworksBarrel = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] starportBeacon = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] meteorTrail = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] rainbowArc = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] crystalCore = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[][] musicBox = new int[BOARD_SIZE][BOARD_SIZE];
    private final int[] propInventory = new int[PROP_COUNT];
    private final int[] propReserve = new int[PROP_COUNT];
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
    private final RectF rankChestRect = new RectF();
    private final RectF dailyChallengeRect = new RectF();
    private final RectF dailyGoalRect = new RectF();
    private final RectF chapterChestRect = new RectF();
    private final RectF replayHintRect = new RectF();
    private final int[] levelStars = new int[LEVEL_COUNT];
    private final int[] levelBestScores = new int[LEVEL_COUNT];
    private final int[] levelRanks = new int[LEVEL_COUNT];
    private final int[] levelFailStreaks = new int[LEVEL_COUNT];
    private final boolean[] levelHiddenChallengesCleared = new boolean[LEVEL_COUNT];
    private final boolean[] levelPerfectCleared = new boolean[LEVEL_COUNT];
    private final boolean[] chapterChestClaimed = new boolean[CHAPTER_COUNT];
    private final boolean[] chapterMasteryClaimed = new boolean[CHAPTER_COUNT];
    private final boolean[] chapterEliteClaimed = new boolean[CHAPTER_COUNT];
    private final boolean[] chapterRankClaimed = new boolean[CHAPTER_COUNT];
    private final boolean[] chapterHiddenClaimed = new boolean[CHAPTER_COUNT];
    private final boolean[] chapterPerfectClaimed = new boolean[CHAPTER_COUNT];
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
            Color.rgb(255, 186, 82),
            Color.rgb(76, 211, 194),
            Color.rgb(238, 122, 159),
            Color.rgb(110, 125, 255),
            Color.rgb(37, 197, 168),
            Color.rgb(255, 116, 196),
            Color.rgb(83, 221, 144),
            Color.rgb(124, 220, 255),
            Color.rgb(255, 148, 136),
            Color.rgb(156, 213, 255),
            Color.rgb(255, 188, 226),
            Color.rgb(255, 202, 116),
            Color.rgb(174, 156, 255),
            Color.rgb(255, 132, 186),
            Color.rgb(112, 222, 196)
    };
    private final int[] chapterBottomColors = {
            Color.rgb(255, 151, 132),
            Color.rgb(255, 196, 112),
            Color.rgb(116, 219, 214),
            Color.rgb(255, 219, 106),
            Color.rgb(255, 139, 176),
            Color.rgb(92, 202, 166),
            Color.rgb(255, 214, 92),
            Color.rgb(117, 182, 255),
            Color.rgb(255, 174, 208),
            Color.rgb(255, 129, 104),
            Color.rgb(106, 225, 255),
            Color.rgb(255, 221, 96),
            Color.rgb(255, 214, 112),
            Color.rgb(124, 226, 168),
            Color.rgb(218, 247, 255),
            Color.rgb(155, 238, 216),
            Color.rgb(255, 238, 150),
            Color.rgb(166, 231, 255),
            Color.rgb(255, 223, 122),
            Color.rgb(255, 168, 122)
    };
    private final String[] chapterNames = {
            "糖果森林", "云朵海湾", "果冻火山", "薄荷花园", "星光梦境", "蜂蜜工坊", "珊瑚集市", "极光城堡", "月光游乐园", "烟花星港", "流星彩虹谷", "奇迹糖晶塔", "泡泡星河岛", "花火薄荷城", "星霜糖果港", "彩云琉璃境", "蜜星钟楼", "极彩乐章谷", "星弦幻音台", "糖彩乐园剧场"
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
    private int coralReefRemaining;
    private int flowerRemaining;
    private int keyRemaining;
    private int honeySpreadCount;
    private int selectedRow = NONE;
    private int selectedCol = NONE;
    private int activeProp = NONE;
    private int hintRowA = NONE;
    private int hintColA = NONE;
    private int hintRowB = NONE;
    private int hintColB = NONE;
    private int idleHintCount;
    private int feedbackCombo;
    private int feedbackCleared;
    private int highestUnlockedLevel;
    private int levelMapPage;
    private int comboEnergy;
    private int comboFeverMoves;
    private int honeyFreezeMoves;
    private int bombShieldCount;
    private int bestCombo;
    private int lastStars;
    private int lastRank;
    private int lastBonusScore;
    private boolean challengeCleared;
    private boolean comboChallengeCleared;
    private boolean scoreChallengeCleared;
    private boolean hiddenChallengeCleared;
    private int coins;
    private int lastCoinReward;
    private int lastAchievementReward;
    private int lastAchievementRewardProp = NONE;
    private int lastAchievementRewardAmount;
    private int winStreak;
    private int lastWinStreakReward;
    private int lastWinStreakRewardProp = NONE;
    private int lastWinStreakRewardAmount;
    private int lastStarUpgradeReward;
    private int lastRankUpgradeReward;
    private int lastRankUpgradeRewardProp = NONE;
    private int lastRankUpgradeRewardAmount;
    private int lastPerfectReward;
    private int lastPerfectRewardProp = NONE;
    private int lastPerfectRewardAmount;
    private boolean lastPerfectRetained;
    private int lastHiddenReward;
    private int lastHiddenRewardProp = NONE;
    private int lastHiddenRewardAmount;
    private int lastEliteReward;
    private int lastFirstClearReward;
    private int lastFullStarReward;
    private int lastReplayReward;
    private int lastReplayRewardProp = NONE;
    private int lastReplayRewardAmount;
    private int lastComebackAssistMoves;
    private int lastComebackAssistProp = NONE;
    private int lastComebackAssistAmount;
    private int starChestClaimed;
    private int rankChestClaimed;
    private int lastChestReward;
    private int lastRankChestReward;
    private int lastChestRewardProp = NONE;
    private int lastChestRewardAmount;
    private int lastChapterChestReward;
    private int lastChapterMasteryReward;
    private int lastChapterEliteReward;
    private int lastChapterRankReward;
    private int lastChapterHiddenReward;
    private int lastChapterPerfectReward;
    private int lastGiftReward;
    private int lastMoveChestReward;
    private int lastCloudReward;
    private int lastFlowerReward;
    private int lastGemReward;
    private int lastGoldenEggReward;
    private int lastCoinPouchReward;
    private int lastPaintBucketReward;
    private int lastWindmillReward;
    private int lastJewelBowReward;
    private int lastStardustJarReward;
    private int lastWishLampReward;
    private int lastResonanceDrumReward;
    private int lastAuroraPrismReward;
    private int lastRainbowBottleReward;
    private int lastEnergyPotionReward;
    private int lastButterflyReward;
    private int lastPortalReward;
    private int lastHourglassReward;
    private int lastLuckyStarRewardProp = NONE;
    private int lastLuckyCloverRewardType;
    private int lastLuckyCloverRewardAmount;
    private int lastLuckyCloverRewardProp = NONE;
    private int lastMysteryRewardType;
    private int lastMysteryRewardAmount;
    private int lastMysteryRewardProp = NONE;
    private int lastPearlReward;
    private int lastCarouselReward;
    private int lastFerrisTicketReward;
    private int lastFireworksBarrelReward;
    private int lastStarportBeaconReward;
    private int lastMeteorTrailReward;
    private int lastRainbowArcReward;
    private int lastCrystalCoreReward;
    private int lastMusicBoxReward;
    private int lastCountdownBombReward;
    private int lastRewardCellMilestoneAmount;
    private int lastEnergyRewardProp = NONE;
    private int lastChestNoticeType;
    private int dailyRewardAmount;
    private int dailyStreak;
    private int lastDailyRewardProp = NONE;
    private int lastDailyRewardPropAmount;
    private int dailyChallengeStreak;
    private int dailyGoalProgress;
    private int seasonLevels;
    private int seasonStars;
    private int seasonRewardStep;
    private int lastSeasonReward;
    private int lastSeasonRewardProp = NONE;
    private int lastSeasonRewardAmount;
    private int lastDailyChallengeMilestoneProp = NONE;
    private int lastDailyChallengeMilestoneAmount;
    private int lastDailyGoalReward;
    private int rewardTargetMilestone;
    private int rewardObstacleMilestone;
    private int rewardComboMilestone;
    private int rewardKeyMilestone;
    private int rewardCellClearedCount;
    private int rewardCellMilestone;
    private int rewardBombMilestone;
    private int rewardMusicBoxMilestone;
    private int lastMusicBoxMilestoneReward;
    private int lastShieldReward;
    private int lastTaskRewardType;
    private long feedbackStartTime;
    private long hintUntilTime;
    private long lastBoardActionTime;
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
    private boolean dailyGoalClaimed;
    private boolean usedContinueThisLevel;
    private boolean countdownBombExploded;
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
        updateIdleHint();
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
            } else if (hasClaimableMapRewardAfterClear()) {
                // 通关后若已有可领奖励，优先回地图领取，避免玩家一路跳关错过奖励反馈。
                levelMapPage = levelIndex / LEVELS_PER_PAGE;
                showingLevelMap = true;
            } else if (levelIndex >= levels.size() - 1) {
                // 最后一关通关后留在地图，方便继续补星和冲评级。
                levelMapPage = levelIndex / LEVELS_PER_PAGE;
                showingLevelMap = true;
            } else {
                startLevel(levelIndex + 1);
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
                usedContinueThisLevel = true;
                levelFailed = false;
                countdownBombExploded = false;
                saveCoins();
                playHaptic(HapticFeedbackConstants.CONFIRM);
                playSuccessTone();
            } else if (useReserveContinue()) {
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
            refreshBoardActionTime();
            invalidate();
            return true;
        }

        int col = (int) ((event.getX() - boardLeft) / tileSize);
        int row = (int) ((event.getY() - boardTop) / tileSize);
        if (!isInside(row, col)) {
            if (activeProp != NONE) {
                // 道具预览状态下点击棋盘外可取消，避免误选后必须使用。
                activeProp = NONE;
            }
            selectedRow = NONE;
            selectedCol = NONE;
            invalidate();
            return true;
        }

        if (activeProp != NONE) {
            refreshBoardActionTime();
            if (selectedRow == row && selectedCol == col) {
                useActiveProp(row, col);
            } else {
                // 点选类道具先预览影响范围，第二次点击同格才确认使用。
                selectedRow = row;
                selectedCol = col;
                playHaptic(HapticFeedbackConstants.CLOCK_TICK);
                playClickTone();
            }
            invalidate();
            return true;
        }

        refreshBoardActionTime();
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
            int brush = i >= 26 && i % 16 == 0 ? 1 : 0;
            int portalProp = i >= 34 && i % 18 == 0 ? 1 : 0;
            int cleanse = i >= 30 && i % 13 == 0 ? 1 : 0;
            int freeze = i >= 36 && i % 15 == 6 ? 1 : 0;
            int magnet = i >= 42 && i % 17 == 8 ? 1 : 0;
            int clock = i >= 60 && i % 19 == 11 ? 1 : 0;
            int starHammer = i >= 92 && i % 20 == 4 ? 1 : 0;
            int rocket = i >= 48 && i % 16 == 5 ? 1 : 0;
            int targetBrush = i >= 66 && i % 18 == 9 ? 1 : 0;
            int shield = i >= 72 && i % 21 == 12 ? 1 : 0;
            int energyCore = i >= 86 && i % 22 == 14 ? 1 : 0;
            int chainBreaker = i >= 74 && i % 18 == 16 ? 1 : 0;
            int lightning = i >= 80 && i % 17 == 13 ? 1 : 0;
            int meteor = i >= 90 && i % 19 == 14 ? 1 : 0;
            int tide = i >= 126 && i % 20 == 6 ? 1 : 0;
            int auroraOrb = i >= 142 && i % 21 == 16 ? 1 : 0;
            int starfishPick = i >= 124 && i % 18 == 8 ? 1 : 0;
            int moonTicket = i >= 164 && i % 16 == 4 ? 1 : 0;
            int fireworkCannon = i >= 184 && i % 15 == 4 ? 1 : 0;
            int starCompass = i >= 190 && i % 17 == 3 ? 1 : 0;
            int bubbleWand = i >= 242 && i % 13 == 8 ? 1 : 0;
            int snowGlobe = i >= 286 && i % 14 == 6 ? 1 : 0;
            int starHarp = i >= 328 && i % 12 == 8 ? 1 : 0;
            int targetKind = i % TILE_KINDS;
            int targetAmount = 8 + (i % 7) + i / 10;
            int iceCount = i < 4 ? i * 2 : Math.min(24, 6 + i / 2);
            int honeyCount = i < 8 ? 0 : Math.min(18, 4 + i / 4);
            int stoneCount = i < 15 ? 0 : Math.min(12, 3 + i / 8);
            int vineCount = i < 25 ? 0 : Math.min(16, 4 + i / 7);
            int giftCount = i < 12 ? 0 : Math.min(8, 2 + i / 18);
            int chainCount = i < 35 ? 0 : Math.min(14, 3 + i / 9);
            int shellCount = i < 45 ? 0 : Math.min(10, 2 + i / 12);
            int flowerCount = i < 52 ? 0 : Math.min(9, 2 + i / 14);
            int coralReefCount = i < 120 || i % 7 != 1 ? 0 : Math.min(7, 2 + (i - 120) / 14);
            int keyCount = i < 28 ? 0 : Math.min(6, 1 + i / 24 + (i % 9 == 0 ? 1 : 0));
            int moveChestCount = i >= 16 && i % 6 == 0 ? 1 + (i % 18 == 0 ? 1 : 0) : 0;
            int cloudCount = i < 18 ? 0 : Math.min(10, 2 + i / 18);
            int gemCount = i < 24 ? 0 : Math.min(7, 1 + i / 20);
            int goldenEggCount = i < 64 || i % 11 != 2 ? 0 : 1;
            int coinPouchCount = i < 88 || i % 15 != 12 ? 0 : 1;
            int paintBucketCount = i < 96 || i % 16 != 7 ? 0 : 1;
            int windmillCount = i < 104 || i % 18 != 13 ? 0 : 1;
            int jewelBowCount = i < 108 || i % 19 != 15 ? 0 : 1;
            int stardustJarCount = i < 112 || i % 20 != 17 ? 0 : 1;
            int wishLampCount = i < 114 || i % 22 != 19 ? 0 : 1;
            int resonanceDrumCount = i < 110 || i % 9 != 2 ? 0 : 1;
            int auroraPrismCount = i < 140 || i % 8 != 3 ? 0 : 1;
            int rainbowBottleCount = i < 70 || i % 12 != 6 ? 0 : 1;
            int energyPotionCount = i < 76 || i % 13 != 9 ? 0 : 1;
            int butterflyCount = i < 82 || i % 14 != 10 ? 0 : 1;
            int portalCount = i < 32 || i % 5 != 1 ? 0 : 2;
            int hourglassCount = i < 38 || i % 7 != 3 ? 0 : 1 + (i % 21 == 3 ? 1 : 0);
            int luckyStarCount = i < 44 || i % 8 != 4 ? 0 : 1;
            int luckyCloverCount = i < 110 || i % 7 != 5 ? 0 : 1;
            int mysteryBoxCount = i < 50 || i % 9 != 5 ? 0 : 1 + (i % 27 == 5 ? 1 : 0);
            int pearlCount = i < 128 || i % 10 != 8 ? 0 : 1;
            int carouselCount = i < 162 || i % 11 != 8 ? 0 : 1;
            int ferrisTicketCount = i < 166 || i % 9 != 4 ? 0 : 1;
            int fireworksBarrelCount = i < 168 || i % 13 != 6 ? 0 : 1;
            int starportBeaconCount = i < 182 || i % 12 != 2 ? 0 : 1;
            int meteorTrailCount = i < 186 || i % 14 != 5 ? 0 : 1;
            int rainbowArcCount = i < 204 || i % 12 != 7 ? 0 : 1;
            int crystalCoreCount = i < 222 || i % 11 != 2 ? 0 : 1;
            int musicBoxCount = i < 328 || i % 10 != 8 ? 0 : 1;
            int countdownBombCount = i < 58 || i % 10 != 7 ? 0 : 1 + (i % 30 == 7 ? 1 : 0);
            if (i >= 240) {
                // 新增两章把后期机关混搭得更密，延长主线和重玩空间。
                meteorTrailCount += i % 5 == 0 ? 1 : 0;
                rainbowArcCount += i % 4 == 1 ? 1 : 0;
                crystalCoreCount += i % 6 == 2 ? 1 : 0;
                starportBeaconCount += i % 7 == 3 ? 1 : 0;
                fireworkCannon += i % 10 == 4 ? 1 : 0;
                starCompass += i % 9 == 5 ? 1 : 0;
                bubbleWand += i % 8 == 6 ? 1 : 0;
            }
            if (i >= 280) {
                // 新扩展两章强调高密度机关连锁，拉长后期刷分和补星空间。
                targetScore += 900 + (i - 280) * 55;
                targetAmount += 4 + (i - 280) / 10;
                moves = Math.max(16, moves + 1);
                shellCount = Math.min(12, shellCount + (i % 4 == 0 ? 1 : 0));
                flowerCount = Math.min(10, flowerCount + (i % 5 == 1 ? 1 : 0));
                coralReefCount = Math.min(8, coralReefCount + (i % 6 == 2 ? 1 : 0));
                auroraPrismCount += i % 5 == 3 ? 1 : 0;
                rainbowBottleCount += i % 6 == 4 ? 1 : 0;
                energyPotionCount += i % 7 == 5 ? 1 : 0;
                luckyCloverCount += i % 8 == 2 ? 1 : 0;
                fireworksBarrelCount += i % 5 == 0 ? 1 : 0;
                starportBeaconCount += i % 4 == 3 ? 1 : 0;
                meteorTrailCount += i % 3 == 1 ? 1 : 0;
                rainbowArcCount += i % 4 == 0 ? 1 : 0;
                crystalCoreCount += i % 5 == 2 ? 1 : 0;
                countdownBombCount += i % 9 == 6 ? 1 : 0;
                fireworkCannon += i % 8 == 2 ? 1 : 0;
                starCompass += i % 7 == 4 ? 1 : 0;
                bubbleWand += i % 6 == 5 ? 1 : 0;
                snowGlobe += i % 9 == 7 ? 1 : 0;
            }
            if (i >= 320) {
                // 末段两章继续提高复合机关密度，形成更长线的补星和冲榜空间。
                targetScore += 1200 + (i - 320) * 65;
                targetAmount += 5 + (i - 320) / 9;
                moves = Math.max(17, moves + 2);
                chainCount = Math.min(16, chainCount + (i % 4 == 1 ? 1 : 0));
                shellCount = Math.min(13, shellCount + (i % 5 == 2 ? 1 : 0));
                flowerCount = Math.min(11, flowerCount + (i % 6 == 3 ? 1 : 0));
                coralReefCount = Math.min(9, coralReefCount + (i % 7 == 4 ? 1 : 0));
                keyCount = Math.min(7, keyCount + (i % 8 == 5 ? 1 : 0));
                moveChestCount += i % 9 == 0 ? 1 : 0;
                stardustJarCount += i % 6 == 1 ? 1 : 0;
                wishLampCount += i % 7 == 2 ? 1 : 0;
                resonanceDrumCount += i % 8 == 3 ? 1 : 0;
                auroraPrismCount += i % 5 == 4 ? 1 : 0;
                rainbowBottleCount += i % 6 == 5 ? 1 : 0;
                energyPotionCount += i % 7 == 6 ? 1 : 0;
                luckyStarCount += i % 8 == 0 ? 1 : 0;
                luckyCloverCount += i % 9 == 1 ? 1 : 0;
                fireworksBarrelCount += i % 4 == 2 ? 1 : 0;
                starportBeaconCount += i % 5 == 3 ? 1 : 0;
                meteorTrailCount += i % 3 == 2 ? 1 : 0;
                rainbowArcCount += i % 4 == 1 ? 1 : 0;
                crystalCoreCount += i % 5 == 0 ? 1 : 0;
                musicBoxCount += i % 6 == 2 ? 1 : 0;
                countdownBombCount += i % 8 == 6 ? 1 : 0;
                fireworkCannon += i % 7 == 2 ? 1 : 0;
                starCompass += i % 6 == 3 ? 1 : 0;
                bubbleWand += i % 5 == 4 ? 1 : 0;
                snowGlobe += i % 6 == 1 ? 1 : 0;
                starHarp += i % 8 == 4 ? 1 : 0;
            }
            if (i >= 360) {
                // 扩展到 400 关后加入更高密度的音乐盒和终章奖励格，强化冲分重玩价值。
                targetScore += 1500 + (i - 360) * 80;
                targetAmount += 6 + (i - 360) / 8;
                moves = Math.max(18, moves + 2);
                chainCount = Math.min(17, chainCount + (i % 5 == 1 ? 1 : 0));
                shellCount = Math.min(14, shellCount + (i % 6 == 2 ? 1 : 0));
                flowerCount = Math.min(12, flowerCount + (i % 7 == 3 ? 1 : 0));
                coralReefCount = Math.min(10, coralReefCount + (i % 8 == 4 ? 1 : 0));
                keyCount = Math.min(8, keyCount + (i % 9 == 5 ? 1 : 0));
                moveChestCount += i % 10 == 0 ? 1 : 0;
                resonanceDrumCount += i % 6 == 1 ? 1 : 0;
                auroraPrismCount += i % 5 == 2 ? 1 : 0;
                rainbowBottleCount += i % 6 == 3 ? 1 : 0;
                energyPotionCount += i % 7 == 4 ? 1 : 0;
                luckyStarCount += i % 8 == 5 ? 1 : 0;
                luckyCloverCount += i % 9 == 6 ? 1 : 0;
                starportBeaconCount += i % 5 == 1 ? 1 : 0;
                meteorTrailCount += i % 4 == 2 ? 1 : 0;
                rainbowArcCount += i % 4 == 3 ? 1 : 0;
                crystalCoreCount += i % 5 == 4 ? 1 : 0;
                musicBoxCount += i % 3 == 0 ? 1 : 0;
                countdownBombCount += i % 7 == 5 ? 1 : 0;
                fireworkCannon += i % 7 == 3 ? 1 : 0;
                starCompass += i % 6 == 4 ? 1 : 0;
                bubbleWand += i % 5 == 0 ? 1 : 0;
                snowGlobe += i % 6 == 2 ? 1 : 0;
                starHarp += i % 5 == 1 ? 1 : 0;
            }
            int moveLimitGoal = i >= 18 && i % 4 == 0 ? Math.max(8, moves - 5) : 0;
            int comboGoal = i >= 22 && i % 5 == 0 ? 3 + (i / 25) : 0;
            int scoreGoal = i >= 30 && i % 6 == 0 ? targetScore + 800 + i * 40 : 0;
            boolean elite = i >= 14 && (i + 1) % 15 == 0;
            if (elite) {
                // 精英关提高一点目标压力，形成每章里更醒目的冲刺节点。
                targetScore += 650 + i * 25;
                targetAmount += 3;
                iceCount = Math.min(26, iceCount + 2);
                honeyCount = Math.min(20, honeyCount + 2);
            }
            levels.add(new Level(targetScore, moves, hammer, bomb, shuffle, rowBlast, colorBlast, extraMoves,
                    magicWand, brush, portalProp, cleanse, freeze, magnet, clock, starHammer, rocket, targetBrush, shield, energyCore, chainBreaker, lightning, meteor, tide, auroraOrb, starfishPick, moonTicket, fireworkCannon, starCompass, bubbleWand, snowGlobe, starHarp, targetKind, targetAmount, iceCount, honeyCount, stoneCount, vineCount, giftCount,
                    chainCount, shellCount, flowerCount, coralReefCount, keyCount, moveChestCount, cloudCount, gemCount, goldenEggCount, coinPouchCount, paintBucketCount, windmillCount, jewelBowCount, stardustJarCount, wishLampCount, resonanceDrumCount, auroraPrismCount, rainbowBottleCount, energyPotionCount, butterflyCount, portalCount, hourglassCount, luckyStarCount, luckyCloverCount, mysteryBoxCount, pearlCount, carouselCount, ferrisTicketCount, fireworksBarrelCount, starportBeaconCount, meteorTrailCount, rainbowArcCount, crystalCoreCount, musicBoxCount, countdownBombCount,
                    moveLimitGoal, comboGoal, scoreGoal, elite));
        }
    }

    private void startLevel(int index) {
        startLevel(index, false);
    }

    private void startLevel(int index, boolean dailyMode) {
        dailyChallengeMode = dailyMode;
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
        comboFeverMoves = 0;
        idleHintCount = 0;
        honeyFreezeMoves = 0;
        bombShieldCount = 0;
        bestCombo = 0;
        lastRank = 0;
        lastCoinReward = 0;
        lastAchievementReward = 0;
        lastAchievementRewardProp = NONE;
        lastAchievementRewardAmount = 0;
        lastWinStreakReward = 0;
        lastWinStreakRewardProp = NONE;
        lastWinStreakRewardAmount = 0;
        lastStarUpgradeReward = 0;
        lastRankUpgradeReward = 0;
        lastRankUpgradeRewardProp = NONE;
        lastRankUpgradeRewardAmount = 0;
        lastPerfectReward = 0;
        lastPerfectRewardProp = NONE;
        lastPerfectRewardAmount = 0;
        lastPerfectRetained = false;
        lastHiddenReward = 0;
        lastHiddenRewardProp = NONE;
        lastHiddenRewardAmount = 0;
        lastEliteReward = 0;
        lastFirstClearReward = 0;
        lastFullStarReward = 0;
        lastReplayReward = 0;
        lastReplayRewardProp = NONE;
        lastReplayRewardAmount = 0;
        lastComebackAssistMoves = 0;
        lastComebackAssistProp = NONE;
        lastComebackAssistAmount = 0;
        lastDailyGoalReward = 0;
        lastSeasonReward = 0;
        lastSeasonRewardProp = NONE;
        lastSeasonRewardAmount = 0;
        lastDailyChallengeMilestoneProp = NONE;
        lastDailyChallengeMilestoneAmount = 0;
        lastChestReward = 0;
        lastRankChestReward = 0;
        lastChestRewardProp = NONE;
        lastChestRewardAmount = 0;
        lastChapterChestReward = 0;
        lastChapterMasteryReward = 0;
        lastChapterEliteReward = 0;
        lastChapterRankReward = 0;
        lastChapterHiddenReward = 0;
        lastChapterPerfectReward = 0;
        lastChestNoticeType = 0;
        lastBoardActionTime = System.currentTimeMillis();
        lastGiftReward = 0;
        lastMoveChestReward = 0;
        lastCloudReward = 0;
        lastFlowerReward = 0;
        lastGemReward = 0;
        lastGoldenEggReward = 0;
        lastCoinPouchReward = 0;
        lastPaintBucketReward = 0;
        lastWindmillReward = 0;
        lastJewelBowReward = 0;
        lastStardustJarReward = 0;
        lastWishLampReward = 0;
        lastResonanceDrumReward = 0;
        lastAuroraPrismReward = 0;
        lastRainbowBottleReward = 0;
        lastEnergyPotionReward = 0;
        lastButterflyReward = 0;
        lastPortalReward = 0;
        lastHourglassReward = 0;
        lastLuckyStarRewardProp = NONE;
        lastLuckyCloverRewardType = 0;
        lastLuckyCloverRewardAmount = 0;
        lastLuckyCloverRewardProp = NONE;
        lastMysteryRewardType = 0;
        lastMysteryRewardAmount = 0;
        lastMysteryRewardProp = NONE;
        lastPearlReward = 0;
        lastCarouselReward = 0;
        lastFerrisTicketReward = 0;
        lastFireworksBarrelReward = 0;
        lastStarportBeaconReward = 0;
        lastMeteorTrailReward = 0;
        lastRainbowArcReward = 0;
        lastCrystalCoreReward = 0;
        lastMusicBoxReward = 0;
        lastMusicBoxMilestoneReward = 0;
        lastCountdownBombReward = 0;
        lastEnergyRewardProp = NONE;
        honeySpreadCount = 0;
        challengeCleared = false;
        comboChallengeCleared = false;
        scoreChallengeCleared = false;
        hiddenChallengeCleared = false;
        usedContinueThisLevel = false;
        countdownBombExploded = false;
        rewardTargetMilestone = 0;
        rewardObstacleMilestone = 0;
        rewardComboMilestone = 0;
        rewardKeyMilestone = 0;
        rewardCellClearedCount = 0;
        rewardCellMilestone = 0;
        rewardBombMilestone = 0;
        rewardMusicBoxMilestone = 0;
        lastShieldReward = 0;
        lastTaskRewardType = 0;
        targetKind = level.targetKind;
        targetRemaining = level.targetAmount;
        iceRemaining = level.iceCount;
        honeyRemaining = level.honeyCount;
        stoneRemaining = level.stoneCount;
        vineRemaining = level.vineCount;
        chainRemaining = level.chainCount;
        shellRemaining = level.shellCount;
        coralReefRemaining = level.coralReefCount;
        flowerRemaining = level.flowerCount;
        keyRemaining = level.keyCount;
        honeySpreadCount = 0;
        propInventory[PROP_HAMMER] = level.hammers;
        propInventory[PROP_BOMB] = level.bombs;
        propInventory[PROP_SHUFFLE] = level.shuffles;
        propInventory[PROP_ROW_BLAST] = level.rowBlasts;
        propInventory[PROP_COLOR_BLAST] = level.colorBlasts;
        propInventory[PROP_EXTRA_MOVES] = level.extraMoves;
        propInventory[PROP_MAGIC_WAND] = level.magicWands;
        propInventory[PROP_BRUSH] = level.brushes;
        propInventory[PROP_PORTAL] = level.portalProps;
        propInventory[PROP_CLEANSE] = level.cleanses;
        propInventory[PROP_FREEZE] = level.freezes;
        propInventory[PROP_MAGNET] = level.magnets;
        propInventory[PROP_CLOCK] = level.clocks;
        propInventory[PROP_STAR_HAMMER] = level.starHammers;
        propInventory[PROP_ROCKET] = level.rockets;
        propInventory[PROP_TARGET_BRUSH] = level.targetBrushes;
        propInventory[PROP_SHIELD] = level.shields;
        propInventory[PROP_ENERGY_CORE] = level.energyCores;
        propInventory[PROP_CHAIN_BREAKER] = level.chainBreakers;
        propInventory[PROP_LIGHTNING] = level.lightnings;
        propInventory[PROP_METEOR] = level.meteors;
        propInventory[PROP_TIDE] = level.tides;
        propInventory[PROP_AURORA_ORB] = level.auroraOrbs;
        propInventory[PROP_STARFISH_PICK] = level.starfishPicks;
        propInventory[PROP_MOON_TICKET] = level.moonTickets;
        propInventory[PROP_FIREWORK_CANNON] = level.fireworkCannons;
        propInventory[PROP_STAR_COMPASS] = level.starCompasses;
        propInventory[PROP_BUBBLE_WAND] = level.bubbleWands;
        propInventory[PROP_SNOW_GLOBE] = level.snowGlobes;
        propInventory[PROP_STAR_HARP] = level.starHarps;
        applyChapterMasteryStarterPerks();
        applyComebackAssist();
        for (int prop = 0; prop < PROP_COUNT; prop++) {
            // 长期奖励道具作为储备带入新关卡，提升收集和回访价值。
            propInventory[prop] += propReserve[prop];
        }

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
                coralReef[row][col] = 0;
                cloud[row][col] = 0;
                flower[row][col] = 0;
                gem[row][col] = 0;
                goldenEgg[row][col] = 0;
                coinPouch[row][col] = 0;
                paintBucket[row][col] = 0;
                windmill[row][col] = 0;
                jewelBow[row][col] = 0;
                stardustJar[row][col] = 0;
                wishLamp[row][col] = 0;
                resonanceDrum[row][col] = 0;
                auroraPrism[row][col] = 0;
                rainbowBottle[row][col] = 0;
                energyPotion[row][col] = 0;
                butterfly[row][col] = 0;
                portal[row][col] = 0;
                hourglass[row][col] = 0;
                luckyStar[row][col] = 0;
                luckyClover[row][col] = 0;
                mysteryBox[row][col] = 0;
                pearl[row][col] = 0;
                carousel[row][col] = 0;
                ferrisTicket[row][col] = 0;
                fireworksBarrel[row][col] = 0;
                starportBeacon[row][col] = 0;
                meteorTrail[row][col] = 0;
                rainbowArc[row][col] = 0;
                crystalCore[row][col] = 0;
                musicBox[row][col] = 0;
                countdownBomb[row][col] = 0;
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
        placeFlower(level.flowerCount);
        placeCoralReef(level.coralReefCount);
        placeKeys(level.keyCount);
        placeMoveChest(level.moveChestCount);
        placeCloud(level.cloudCount);
        placeGem(level.gemCount);
        placeGoldenEgg(level.goldenEggCount);
        placeCoinPouch(level.coinPouchCount);
        placePaintBucket(level.paintBucketCount);
        placeWindmill(level.windmillCount);
        placeJewelBow(level.jewelBowCount);
        placeStardustJar(level.stardustJarCount);
        placeWishLamp(level.wishLampCount);
        placeResonanceDrum(level.resonanceDrumCount);
        placeAuroraPrism(level.auroraPrismCount);
        placeRainbowBottle(level.rainbowBottleCount);
        placeEnergyPotion(level.energyPotionCount);
        placeButterfly(level.butterflyCount);
        placePortal(level.portalCount);
        placeHourglass(level.hourglassCount);
        placeLuckyStar(level.luckyStarCount);
        placeLuckyClover(level.luckyCloverCount);
        placeMysteryBox(level.mysteryBoxCount);
        placePearl(level.pearlCount);
        placeCarousel(level.carouselCount);
        placeFerrisTicket(level.ferrisTicketCount);
        placeFireworksBarrel(level.fireworksBarrelCount);
        placeStarportBeacon(level.starportBeaconCount);
        placeMeteorTrail(level.meteorTrailCount);
        placeRainbowArc(level.rainbowArcCount);
        placeCrystalCore(level.crystalCoreCount);
        placeMusicBox(level.musicBoxCount);
        placeCountdownBomb(level.countdownBombCount, Math.max(5, level.moves / 2));
        ensurePlayableBoard();
        levelIntroUntilTime = System.currentTimeMillis() + 1400;
    }

    private void startDailyChallenge() {
        long today = getToday();
        int challengeIndex = (int) ((today * 37 + 11) % levels.size());
        startLevel(challengeIndex, true);
        // 每日挑战复用关卡池，但奖励和通关不推进主线。
        movesLeft = Math.max(12, movesLeft - 3);
        movesUsed = 0;
        score = 0;
    }

    private boolean useReserveContinue() {
        int prop = pickContinueProp();
        if (prop == NONE) {
            return false;
        }

        // 金币不足时允许消耗储备道具续步，让长期奖励在失败时也有救场价值。
        consumeProp(prop);
        movesLeft = prop == PROP_EXTRA_MOVES ? 5 : 3;
        moveLimitBonus += movesLeft;
        usedContinueThisLevel = true;
        levelFailed = false;
        countdownBombExploded = false;
        if (prop == PROP_SNOW_GLOBE) {
            honeyFreezeMoves = Math.max(honeyFreezeMoves, 3);
            extendCountdownBombs(3);
        } else if (prop == PROP_SHIELD) {
            bombShieldCount++;
            extendCountdownBombs(2);
        }
        showFeedback(1, movesLeft);
        return true;
    }

    private int pickContinueProp() {
        int[] props = {PROP_EXTRA_MOVES, PROP_MOON_TICKET, PROP_SNOW_GLOBE, PROP_SHIELD, PROP_CLOCK};
        for (int prop : props) {
            if (propInventory[prop] > 0) {
                return prop;
            }
        }
        return NONE;
    }

    private void addProp(int prop, int amount) {
        if (prop == NONE || amount <= 0) {
            return;
        }
        propInventory[prop] += amount;
    }

    private void addReserveProp(int prop, int amount) {
        if (prop == NONE || amount <= 0) {
            return;
        }
        propInventory[prop] += amount;
        propReserve[prop] += amount;
        savePropReserve(prop);
    }

    private void consumeProp(int prop) {
        propInventory[prop]--;
        int remaining = Math.max(0, propInventory[prop]);
        if (propReserve[prop] > remaining) {
            propReserve[prop] = remaining;
            savePropReserve(prop);
        }
    }

    private void savePropReserve(int prop) {
        prefs.edit().putInt(KEY_PROP_RESERVE_PREFIX + prop, propReserve[prop]).apply();
    }

    private boolean handlePropTap(float x, float y) {
        for (int prop = 0; prop < PROP_COUNT; prop++) {
            RectF rect = propRects[prop];
            if (rect != null && rect.contains(x, y)) {
                if (propInventory[prop] <= 0) {
                    if (coins < PROP_COSTS[prop]) {
                        activeProp = NONE;
                        selectedRow = NONE;
                        selectedCol = NONE;
                        lastTaskRewardType = 21;
                        showFeedback(1, PROP_COSTS[prop] - coins);
                        playHaptic(HapticFeedbackConstants.REJECT);
                        playRejectTone();
                        return true;
                    }
                    // 道具用完后可直接用金币补一个，减少关卡中断感。
                    coins -= PROP_COSTS[prop];
                    propInventory[prop]++;
                    lastTaskRewardType = 22;
                    showFeedback(1, PROP_COSTS[prop]);
                    // 金币临时购买只用于当前关，不写入长期储备。
                    saveCoins();
                }

                if (prop == PROP_SHUFFLE) {
                    consumeProp(prop);
                    shuffleBoard();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_PORTAL) {
                    // 传送道具即时扰动棋盘，适合主动寻找新连锁。
                    consumeProp(prop);
                    triggerPortalShift();
                    resolveMatches(findMatches());
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_EXTRA_MOVES) {
                    // 加步道具即时生效，适合低步数时救场。
                    consumeProp(prop);
                    movesLeft += 5;
                    moveLimitBonus += 5;
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_FREEZE) {
                    // 冻结道具暂停蜂蜜蔓延，给高压局面留出规划窗口。
                    consumeProp(prop);
                    honeyFreezeMoves = 4;
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_MAGNET) {
                    // 磁铁直接吸走当前目标色，帮助玩家补齐收集目标。
                    consumeProp(prop);
                    clearCells(buildColorCells(targetKind), 160);
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_CLOCK) {
                    // 时钟补步并延缓炸弹倒计时，专门应对后期高压关卡。
                    consumeProp(prop);
                    movesLeft += 3;
                    moveLimitBonus += 3;
                    extendCountdownBombs(2);
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_SHIELD) {
                    // 护盾即时生效，能抵消一次倒计时炸弹归零。
                    consumeProp(prop);
                    bombShieldCount++;
                    extendCountdownBombs(1);
                    lastShieldReward = bombShieldCount;
                    lastTaskRewardType = 10;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    lastStarportBeaconReward = 0;
                    lastMeteorTrailReward = 0;
                    lastRainbowArcReward = 0;
                    lastCrystalCoreReward = 0;
                    lastMusicBoxReward = 0;
                    showFeedback(1, bombShieldCount);
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_ENERGY_CORE) {
                    // 能量核心直接填满能量并补给随机道具，制造一次主动爆发机会。
                    consumeProp(prop);
                    comboEnergy = 100;
                    lastEnergyRewardProp = random.nextInt(PROP_COUNT);
                    addProp(lastEnergyRewardProp, 1);
                    lastTaskRewardType = 11;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    lastStarportBeaconReward = 0;
                    lastMeteorTrailReward = 0;
                    lastRainbowArcReward = 0;
                    lastCrystalCoreReward = 0;
                    lastMusicBoxReward = 0;
                    showFeedback(1, 100);
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_METEOR) {
                    // 流星随机砸开多处棋盘格，适合临近失败时制造翻盘机会。
                    consumeProp(prop);
                    clearCells(buildRandomCells(8), 240);
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_TIDE) {
                    // 潮汐横扫多行棋盘，适合后期大面积打开局面。
                    consumeProp(prop);
                    clearCells(buildTideCells(), 260);
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_AURORA_ORB) {
                    // 极光球补满能量并生成彩虹棋，适合极光章节制造爆发。
                    consumeProp(prop);
                    comboEnergy = 100;
                    upgradeRandomRainbowPiece();
                    lastTaskRewardType = 13;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    lastStarportBeaconReward = 0;
                    lastMeteorTrailReward = 0;
                    lastRainbowArcReward = 0;
                    lastCrystalCoreReward = 0;
                    lastMusicBoxReward = 0;
                    showFeedback(1, 100);
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_STARFISH_PICK) {
                    // 海星镐随机敲开多层障碍，专门应对后期珊瑚礁和贝壳压力。
                    consumeProp(prop);
                    int chipped = chipLayeredObstacles(5);
                    if (chipped <= 0) {
                        clearCells(buildRandomCells(3), 120);
                    } else {
                        grantTaskRewards();
                    }
                    lastTaskRewardType = 14;
                    lastFlowerReward = 0;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    lastStarportBeaconReward = 0;
                    lastMeteorTrailReward = 0;
                    lastRainbowArcReward = 0;
                    lastCrystalCoreReward = 0;
                    lastMusicBoxReward = 0;
                    showFeedback(1, Math.max(1, chipped));
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_MOON_TICKET) {
                    // 月光票券补步并送一枚方向特效，作为终章关卡的轻量翻盘道具。
                    consumeProp(prop);
                    movesLeft += 2;
                    moveLimitBonus += 2;
                    clearCells(buildRandomCells(4), 160);
                    upgradeRandomDirectionalPiece();
                    lastTaskRewardType = 15;
                    lastGiftReward = 0;
                    lastMoveChestReward = 0;
                    lastCloudReward = 0;
                    lastFlowerReward = 0;
                    lastGemReward = 0;
                    lastGoldenEggReward = 0;
                    lastCoinPouchReward = 0;
                    lastPaintBucketReward = 0;
                    lastWindmillReward = 0;
                    lastJewelBowReward = 0;
                    lastStardustJarReward = 0;
                    lastWishLampReward = 0;
                    lastResonanceDrumReward = 0;
                    lastAuroraPrismReward = 0;
                    lastRainbowBottleReward = 0;
                    lastEnergyPotionReward = 0;
                    lastButterflyReward = 0;
                    lastPortalReward = 0;
                    lastHourglassReward = 0;
                    lastLuckyStarRewardProp = NONE;
                    lastLuckyCloverRewardType = 0;
                    lastMysteryRewardType = 0;
                    lastPearlReward = 0;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    lastStarportBeaconReward = 0;
                    lastMeteorTrailReward = 0;
                    lastRainbowArcReward = 0;
                    lastCrystalCoreReward = 0;
                    lastMusicBoxReward = 0;
                    showFeedback(1, 2);
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_FIREWORK_CANNON) {
                    // 烟花礼炮直接点燃多处爆点，适合烟花星港后段制造爽快连锁。
                    consumeProp(prop);
                    clearCells(buildRandomCells(6), 220);
                    upgradeRandomBombPiece();
                    upgradeRandomBombPiece();
                    comboEnergy = Math.min(100, comboEnergy + 30);
                    lastTaskRewardType = 16;
                    lastGiftReward = 0;
                    lastMoveChestReward = 0;
                    lastCloudReward = 0;
                    lastFlowerReward = 0;
                    lastGemReward = 0;
                    lastGoldenEggReward = 0;
                    lastCoinPouchReward = 0;
                    lastPaintBucketReward = 0;
                    lastWindmillReward = 0;
                    lastJewelBowReward = 0;
                    lastStardustJarReward = 0;
                    lastWishLampReward = 0;
                    lastResonanceDrumReward = 0;
                    lastAuroraPrismReward = 0;
                    lastRainbowBottleReward = 0;
                    lastEnergyPotionReward = 0;
                    lastButterflyReward = 0;
                    lastPortalReward = 0;
                    lastHourglassReward = 0;
                    lastLuckyStarRewardProp = NONE;
                    lastLuckyCloverRewardType = 0;
                    lastMysteryRewardType = 0;
                    lastPearlReward = 0;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    lastStarportBeaconReward = 0;
                    lastMeteorTrailReward = 0;
                    lastRainbowArcReward = 0;
                    lastCrystalCoreReward = 0;
                    lastMusicBoxReward = 0;
                    showFeedback(1, 30);
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_BUBBLE_WAND) {
                    // 泡泡棒即时净化多处障碍并送一个彩虹棋，适合新增星河岛后期混搭关翻盘。
                    consumeProp(prop);
                    int cleaned = cleanseRandomObstacles(6);
                    if (cleaned <= 0) {
                        clearCells(buildRandomCells(4), 180);
                    } else {
                        score += cleaned * 75;
                        grantTaskRewards();
                    }
                    upgradeRandomRainbowPiece();
                    comboEnergy = Math.min(100, comboEnergy + 24);
                    lastTaskRewardType = 18;
                    lastGiftReward = 0;
                    lastMoveChestReward = 0;
                    lastCloudReward = 0;
                    lastFlowerReward = 0;
                    lastGemReward = 0;
                    lastGoldenEggReward = 0;
                    lastCoinPouchReward = 0;
                    lastPaintBucketReward = 0;
                    lastWindmillReward = 0;
                    lastJewelBowReward = 0;
                    lastStardustJarReward = 0;
                    lastWishLampReward = 0;
                    lastResonanceDrumReward = 0;
                    lastAuroraPrismReward = 0;
                    lastRainbowBottleReward = 0;
                    lastEnergyPotionReward = 0;
                    lastButterflyReward = 0;
                    lastPortalReward = 0;
                    lastHourglassReward = 0;
                    lastLuckyStarRewardProp = NONE;
                    lastLuckyCloverRewardType = 0;
                    lastMysteryRewardType = 0;
                    lastPearlReward = 0;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    lastStarportBeaconReward = 0;
                    lastMeteorTrailReward = 0;
                    lastRainbowArcReward = 0;
                    lastCrystalCoreReward = 0;
                    lastMusicBoxReward = 0;
                    showFeedback(1, Math.max(1, cleaned));
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_SNOW_GLOBE) {
                    // 雪花球冻结蜂蜜和炸弹，同时削弱多层障碍，适合终盘稳住局面。
                    consumeProp(prop);
                    honeyFreezeMoves = Math.max(honeyFreezeMoves, 5);
                    extendCountdownBombs(3);
                    int chipped = chipLayeredObstacles(6);
                    int cleaned = cleanseRandomObstacles(4);
                    if (chipped + cleaned <= 0) {
                        clearCells(buildRandomCells(5), 190);
                    } else {
                        score += cleaned * 70 + chipped * 90;
                        grantTaskRewards();
                    }
                    comboEnergy = Math.min(100, comboEnergy + 18);
                    lastTaskRewardType = 19;
                    lastGiftReward = 0;
                    lastMoveChestReward = 0;
                    lastCloudReward = 0;
                    lastFlowerReward = 0;
                    lastGemReward = 0;
                    lastGoldenEggReward = 0;
                    lastCoinPouchReward = 0;
                    lastPaintBucketReward = 0;
                    lastWindmillReward = 0;
                    lastJewelBowReward = 0;
                    lastStardustJarReward = 0;
                    lastWishLampReward = 0;
                    lastResonanceDrumReward = 0;
                    lastAuroraPrismReward = 0;
                    lastRainbowBottleReward = 0;
                    lastEnergyPotionReward = 0;
                    lastButterflyReward = 0;
                    lastPortalReward = 0;
                    lastHourglassReward = 0;
                    lastLuckyStarRewardProp = NONE;
                    lastLuckyCloverRewardType = 0;
                    lastMysteryRewardType = 0;
                    lastPearlReward = 0;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    lastStarportBeaconReward = 0;
                    lastMeteorTrailReward = 0;
                    lastRainbowArcReward = 0;
                    lastCrystalCoreReward = 0;
                    lastMusicBoxReward = 0;
                    showFeedback(1, Math.max(1, chipped + cleaned));
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_STAR_HARP) {
                    // 星弦竖琴直接奏出三枚特效棋，适合最终章主动铺开连续爆发。
                    consumeProp(prop);
                    upgradeRandomDirectionalPiece();
                    upgradeRandomRainbowPiece();
                    upgradeRandomBombPiece();
                    comboEnergy = Math.min(100, comboEnergy + 36);
                    lastTaskRewardType = 20;
                    lastGiftReward = 0;
                    lastMoveChestReward = 0;
                    lastCloudReward = 0;
                    lastFlowerReward = 0;
                    lastGemReward = 0;
                    lastGoldenEggReward = 0;
                    lastCoinPouchReward = 0;
                    lastPaintBucketReward = 0;
                    lastWindmillReward = 0;
                    lastJewelBowReward = 0;
                    lastStardustJarReward = 0;
                    lastWishLampReward = 0;
                    lastResonanceDrumReward = 0;
                    lastAuroraPrismReward = 0;
                    lastRainbowBottleReward = 0;
                    lastEnergyPotionReward = 0;
                    lastButterflyReward = 0;
                    lastPortalReward = 0;
                    lastHourglassReward = 0;
                    lastLuckyStarRewardProp = NONE;
                    lastLuckyCloverRewardType = 0;
                    lastMysteryRewardType = 0;
                    lastPearlReward = 0;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    lastStarportBeaconReward = 0;
                    lastMeteorTrailReward = 0;
                    lastRainbowArcReward = 0;
                    lastCrystalCoreReward = 0;
                    lastMusicBoxReward = 0;
                    showFeedback(1, 36);
                    checkLevelState();
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
            consumeProp(PROP_HAMMER);
            clearCells(buildSingleCell(row, col), 90);
        } else if (activeProp == PROP_BOMB) {
            consumeProp(PROP_BOMB);
            clearCells(buildBombCells(row, col), 140);
        } else if (activeProp == PROP_ROW_BLAST) {
            consumeProp(PROP_ROW_BLAST);
            clearCells(buildCrossCells(row, col), 180);
        } else if (activeProp == PROP_COLOR_BLAST) {
            consumeProp(PROP_COLOR_BLAST);
            clearCells(buildColorCells(colorOf(board[row][col])), 220);
        } else if (activeProp == PROP_ROCKET) {
            // 火箭按点击格的奇偶方向清一行或一列，适合精确打开局面。
            consumeProp(PROP_ROCKET);
            clearCells(buildRocketCells(row, col), 190);
        } else if (activeProp == PROP_LIGHTNING) {
            // 闪电沿两条对角线劈开棋盘，适合打开被斜向隔断的局面。
            consumeProp(PROP_LIGHTNING);
            clearCells(buildDiagonalCells(row, col), 210);
        } else if (activeProp == PROP_STAR_COMPASS) {
            // 星轨罗盘同时划开十字和双对角线，给终章复杂棋盘一个强力定点解法。
            consumeProp(PROP_STAR_COMPASS);
            lastTaskRewardType = 17;
            clearCells(buildStarCompassCells(row, col), 320);
            lastTaskRewardType = 17;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGemReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastPortalReward = 0;
            lastHourglassReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, 1);
        } else if (activeProp == PROP_TARGET_BRUSH) {
            // 目标刷把小范围棋子染成目标色，帮助收集关续上消除机会。
            consumeProp(PROP_TARGET_BRUSH);
            int painted = paintTargetBrushCells(row, col);
            spawnParticles(buildBombCells(row, col));
            lastTaskRewardType = 9;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGemReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastPortalReward = 0;
            lastHourglassReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, Math.max(1, painted));
        } else if (activeProp == PROP_MAGIC_WAND) {
            // 魔法棒把指定棋子升级成彩虹棋，让玩家主动创造关键大招。
            consumeProp(PROP_MAGIC_WAND);
            board[row][col] = makePiece(colorOf(board[row][col]), SPECIAL_RAINBOW);
            spawnParticles(buildSingleCell(row, col));
            lastTaskRewardType = 5;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGemReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastPortalReward = 0;
            lastHourglassReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, 1);
        } else if (activeProp == PROP_BRUSH) {
            // 克隆刷把普通棋升级成方向特效，方便玩家主动铺垫连锁。
            consumeProp(PROP_BRUSH);
            int special = (row + col + movesUsed) % 2 == 0 ? SPECIAL_ROW : SPECIAL_COLUMN;
            board[row][col] = makePiece(colorOf(board[row][col]), special);
            spawnParticles(buildSingleCell(row, col));
            lastTaskRewardType = 6;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGemReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastPortalReward = 0;
            lastHourglassReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, 1);
        } else if (activeProp == PROP_STAR_HAMMER) {
            // 星锤把指定棋子锤成爆炸特效，用来主动制造更大的连锁。
            consumeProp(PROP_STAR_HAMMER);
            board[row][col] = makePiece(colorOf(board[row][col]), SPECIAL_BOMB);
            spawnParticles(buildSingleCell(row, col));
            lastTaskRewardType = 8;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGemReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastPortalReward = 0;
            lastHourglassReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, 1);
        } else if (activeProp == PROP_CLEANSE) {
            // 净化道具直接削弱周围障碍，适合处理蜂蜜和多层阻挡。
            consumeProp(PROP_CLEANSE);
            int cleaned = cleanseAround(row, col);
            score += cleaned * 70;
            spawnParticles(buildBombCells(row, col));
            grantTaskRewards();
            lastTaskRewardType = 7;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGemReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastPortalReward = 0;
            lastHourglassReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, Math.max(1, cleaned));
        } else if (activeProp == PROP_CHAIN_BREAKER) {
            // 破锁钳专门剪开锁链和藤蔓，帮助玩家快速打开被封住的区域。
            consumeProp(PROP_CHAIN_BREAKER);
            int broken = breakChainsAround(row, col);
            score += broken * 80;
            spawnParticles(buildBombCells(row, col));
            grantTaskRewards();
            lastTaskRewardType = 12;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGemReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastPortalReward = 0;
            lastHourglassReward = 0;
            lastLuckyStarRewardProp = NONE;
            lastLuckyCloverRewardType = 0;
            lastLuckyCloverRewardAmount = 0;
            lastLuckyCloverRewardProp = NONE;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, Math.max(1, broken));
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
            consumeComboFeverMove();
            clearCells(buildSpecialComboCells(selectedRow, selectedCol, row, col), 360);
            spreadHoneyAfterMove();
            checkLevelState();
            tickCountdownBombs();
            playHaptic(HapticFeedbackConstants.CONFIRM);
            playSuccessTone();
            selectedRow = NONE;
            selectedCol = NONE;
            return;
        }
        if (specialOf(fromPiece) != SPECIAL_NORMAL || specialOf(toPiece) != SPECIAL_NORMAL) {
            movesLeft--;
            movesUsed++;
            consumeComboFeverMove();
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
            tickCountdownBombs();
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
            consumeComboFeverMove();
            createSpecialFromMatch(matches, row, col);
            resolveMatches(matches);
            spreadHoneyAfterMove();
            checkLevelState();
            tickCountdownBombs();
            playHaptic(HapticFeedbackConstants.CONFIRM);
            playSuccessTone();
        }
        selectedRow = NONE;
        selectedCol = NONE;
    }

    private void clearCells(Set<Cell> cells, int bonusScore) {
        lastMoveChestReward = 0;
        lastCloudReward = 0;
        lastFlowerReward = 0;
        lastGemReward = 0;
        lastGoldenEggReward = 0;
        lastCoinPouchReward = 0;
        lastPaintBucketReward = 0;
        lastWindmillReward = 0;
        lastJewelBowReward = 0;
        lastStardustJarReward = 0;
        lastWishLampReward = 0;
        lastResonanceDrumReward = 0;
        lastAuroraPrismReward = 0;
        lastRainbowBottleReward = 0;
        lastEnergyPotionReward = 0;
        lastButterflyReward = 0;
        lastPortalReward = 0;
        lastHourglassReward = 0;
        lastLuckyStarRewardProp = NONE;
        lastLuckyCloverRewardType = 0;
        lastLuckyCloverRewardAmount = 0;
        lastLuckyCloverRewardProp = NONE;
        lastMysteryRewardType = 0;
        lastMysteryRewardAmount = 0;
        lastMysteryRewardProp = NONE;
        lastPearlReward = 0;
        lastCarouselReward = 0;
        lastFerrisTicketReward = 0;
        lastFireworksBarrelReward = 0;
        lastStarportBeaconReward = 0;
        lastMeteorTrailReward = 0;
        lastRainbowArcReward = 0;
        lastCrystalCoreReward = 0;
        lastMusicBoxReward = 0;
        lastCountdownBombReward = 0;
        cells = expandSpecialCells(cells);
        score += applyComboFeverScore(bonusScore + cells.size() * 45);
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

    private Set<Cell> buildRandomCells(int count) {
        Set<Cell> cells = new HashSet<>();
        while (cells.size() < count && cells.size() < BOARD_SIZE * BOARD_SIZE) {
            cells.add(new Cell(random.nextInt(BOARD_SIZE), random.nextInt(BOARD_SIZE)));
        }
        return cells;
    }

    private Set<Cell> buildTideCells() {
        Set<Cell> cells = new HashSet<>();
        int startRow = random.nextInt(BOARD_SIZE - 2);
        for (int row = startRow; row < startRow + 3; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                cells.add(new Cell(row, col));
            }
        }
        return cells;
    }

    private int cleanseAround(int row, int col) {
        int cleaned = 0;
        for (Cell cell : buildBombCells(row, col)) {
            cleaned += cleanseCell(cell.row, cell.col);
        }
        return cleaned;
    }

    private int breakChainsAround(int row, int col) {
        int broken = 0;
        for (Cell cell : buildBombCells(row, col)) {
            if (vine[cell.row][cell.col] > 0) {
                vine[cell.row][cell.col] = 0;
                vineRemaining--;
                broken++;
            }
            if (chain[cell.row][cell.col] > 0) {
                chain[cell.row][cell.col] = 0;
                chainRemaining--;
                broken++;
            }
        }
        return broken;
    }

    private int cleanseRandomObstacles(int count) {
        List<Cell> candidates = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (ice[row][col] > 0 || honey[row][col] > 0 || stone[row][col] > 0
                        || vine[row][col] > 0 || chain[row][col] > 0 || shell[row][col] > 0
                        || coralReef[row][col] > 0 || flower[row][col] > 0) {
                    candidates.add(new Cell(row, col));
                }
            }
        }

        int cleaned = 0;
        while (cleaned < count && !candidates.isEmpty()) {
            Cell cell = candidates.remove(random.nextInt(candidates.size()));
            int cellCleaned = cleanseCell(cell.row, cell.col);
            if (cellCleaned > 0) {
                cleaned += cellCleaned;
                spawnParticles(buildSingleCell(cell.row, cell.col));
            }
        }
        return cleaned;
    }

    private int chipLayeredObstacles(int count) {
        List<Cell> candidates = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (shell[row][col] > 0 || flower[row][col] > 0 || coralReef[row][col] > 0) {
                    candidates.add(new Cell(row, col));
                }
            }
        }

        int chipped = 0;
        while (chipped < count && !candidates.isEmpty()) {
            Cell cell = candidates.remove(random.nextInt(candidates.size()));
            if (chipLayeredCell(cell.row, cell.col)) {
                chipped++;
                spawnParticles(buildSingleCell(cell.row, cell.col));
            }
        }
        return chipped;
    }

    private boolean chipLayeredCell(int row, int col) {
        if (coralReef[row][col] > 0) {
            coralReef[row][col]--;
            if (coralReef[row][col] == 0) {
                coralReefRemaining--;
                score += 140;
            }
            return true;
        }
        if (shell[row][col] > 0) {
            shell[row][col]--;
            if (shell[row][col] == 0) {
                shellRemaining--;
            }
            return true;
        }
        if (flower[row][col] > 0) {
            flower[row][col]--;
            if (flower[row][col] == 0) {
                flowerRemaining--;
                score += 160;
                lastFlowerReward += 160;
            }
            return true;
        }
        return false;
    }

    private int cleanseCell(int row, int col) {
        int cleaned = 0;
        if (ice[row][col] > 0) {
            cleaned++;
            ice[row][col] = 0;
            iceRemaining--;
        }
        if (honey[row][col] > 0) {
            cleaned++;
            honey[row][col] = 0;
            honeyRemaining--;
        }
        if (vine[row][col] > 0) {
            cleaned++;
            vine[row][col] = 0;
            vineRemaining--;
        }
        if (chain[row][col] > 0) {
            cleaned++;
            chain[row][col] = 0;
            chainRemaining--;
        }
        if (stone[row][col] > 0) {
            cleaned += stone[row][col];
            stone[row][col] = 0;
            stoneRemaining--;
        }
        if (shell[row][col] > 0) {
            cleaned += shell[row][col];
            shell[row][col] = 0;
            shellRemaining--;
        }
        if (coralReef[row][col] > 0) {
            cleaned += coralReef[row][col];
            coralReef[row][col] = 0;
            coralReefRemaining--;
        }
        if (flower[row][col] > 0) {
            cleaned += flower[row][col];
            flower[row][col] = 0;
            flowerRemaining--;
        }
        return cleaned;
    }

    private Set<Cell> buildCrossCells(int row, int col) {
        Set<Cell> cells = new HashSet<>();
        for (int index = 0; index < BOARD_SIZE; index++) {
            cells.add(new Cell(row, index));
            cells.add(new Cell(index, col));
        }
        return cells;
    }

    private Set<Cell> buildDiagonalCells(int row, int col) {
        Set<Cell> cells = new HashSet<>();
        for (int delta = -BOARD_SIZE; delta <= BOARD_SIZE; delta++) {
            int firstRow = row + delta;
            int firstCol = col + delta;
            if (isInside(firstRow, firstCol)) {
                cells.add(new Cell(firstRow, firstCol));
            }
            int secondRow = row + delta;
            int secondCol = col - delta;
            if (isInside(secondRow, secondCol)) {
                cells.add(new Cell(secondRow, secondCol));
            }
        }
        return cells;
    }

    private Set<Cell> buildStarCompassCells(int row, int col) {
        Set<Cell> cells = buildCrossCells(row, col);
        cells.addAll(buildDiagonalCells(row, col));
        return cells;
    }

    private Set<Cell> buildRocketCells(int row, int col) {
        Set<Cell> cells = new HashSet<>();
        if ((row + col + movesUsed) % 2 == 0) {
            for (int sweepCol = 0; sweepCol < BOARD_SIZE; sweepCol++) {
                cells.add(new Cell(row, sweepCol));
            }
        } else {
            for (int sweepRow = 0; sweepRow < BOARD_SIZE; sweepRow++) {
                cells.add(new Cell(sweepRow, col));
            }
        }
        return cells;
    }

    private int paintTargetBrushCells(int row, int col) {
        int painted = 0;
        for (Cell cell : buildBombCells(row, col)) {
            if (board[cell.row][cell.col] != NONE && specialOf(board[cell.row][cell.col]) == SPECIAL_NORMAL) {
                board[cell.row][cell.col] = makePiece(targetKind, SPECIAL_NORMAL);
                painted++;
            }
        }
        return painted;
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
        lastCloudReward = 0;
        lastFlowerReward = 0;
        lastGemReward = 0;
        lastGoldenEggReward = 0;
        lastCoinPouchReward = 0;
        lastPaintBucketReward = 0;
        lastWindmillReward = 0;
        lastJewelBowReward = 0;
        lastStardustJarReward = 0;
        lastWishLampReward = 0;
        lastResonanceDrumReward = 0;
        lastAuroraPrismReward = 0;
        lastRainbowBottleReward = 0;
        lastEnergyPotionReward = 0;
        lastButterflyReward = 0;
        lastPortalReward = 0;
        lastHourglassReward = 0;
        lastLuckyStarRewardProp = NONE;
        lastLuckyCloverRewardType = 0;
        lastLuckyCloverRewardAmount = 0;
        lastLuckyCloverRewardProp = NONE;
        lastMysteryRewardType = 0;
        lastMysteryRewardAmount = 0;
        lastMysteryRewardProp = NONE;
        lastPearlReward = 0;
        lastCarouselReward = 0;
        lastFerrisTicketReward = 0;
        lastFireworksBarrelReward = 0;
        lastStarportBeaconReward = 0;
        lastMeteorTrailReward = 0;
        lastRainbowArcReward = 0;
        lastCrystalCoreReward = 0;
        lastMusicBoxReward = 0;
        while (!matches.isEmpty()) {
            combo++;
            matches = expandSpecialCells(matches);
            totalCleared += matches.size();
            score += applyComboFeverScore(matches.size() * 60 + (combo - 1) * 120);
            spawnParticles(matches);
            spawnComboBurst(combo, matches);
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
                comboFeverMoves = 3;
                lastEnergyRewardProp = random.nextInt(PROP_COUNT);
                addProp(lastEnergyRewardProp, 1);
                lastTaskRewardType = 3;
                lastGiftReward = 0;
                honeySpreadCount = 0;
                lastMysteryRewardType = 0;
                lastMysteryRewardAmount = 0;
                lastMysteryRewardProp = NONE;
                lastPearlReward = 0;
                lastCarouselReward = 0;
                lastFerrisTicketReward = 0;
                lastFireworksBarrelReward = 0;
                lastStarportBeaconReward = 0;
                lastMeteorTrailReward = 0;
                lastRainbowArcReward = 0;
                lastCrystalCoreReward = 0;
                lastMusicBoxReward = 0;
                showFeedback(combo + 1, totalCleared);
            } else if (lastTaskRewardType == 0) {
                showNormalFeedback(combo, totalCleared);
            }
        }
    }

    private int applyComboFeverScore(int baseScore) {
        return comboFeverMoves > 0 ? (int) (baseScore * 1.25f) : baseScore;
    }

    private void consumeComboFeverMove() {
        if (comboFeverMoves > 0) {
            comboFeverMoves--;
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
        Move bestMove = null;
        int bestScore = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (col + 1 < BOARD_SIZE) {
                    Move move = scoreCandidateMove(row, col, row, col + 1);
                    if (move.score > bestScore) {
                        bestMove = move;
                        bestScore = move.score;
                    }
                }
                if (row + 1 < BOARD_SIZE) {
                    Move move = scoreCandidateMove(row, col, row + 1, col);
                    if (move.score > bestScore) {
                        bestMove = move;
                        bestScore = move.score;
                    }
                }
            }
        }
        return bestMove;
    }

    private boolean wouldCreateMatch(int rowA, int colA, int rowB, int colB) {
        return scoreCandidateMove(rowA, colA, rowB, colB).score > 0;
    }

    private Move scoreCandidateMove(int rowA, int colA, int rowB, int colB) {
        swap(rowA, colA, rowB, colB);
        Set<Cell> matches = findMatches();
        int score = 0;
        if (!matches.isEmpty()) {
            score = matches.size() * 12;
            for (Cell cell : matches) {
                if (colorOf(board[cell.row][cell.col]) == targetKind) {
                    score += 9;
                }
                score += getHintCellPriority(cell.row, cell.col);
                if (hasAdjacentObstacle(cell.row, cell.col)) {
                    score += 7;
                }
                if (specialOf(board[cell.row][cell.col]) != SPECIAL_NORMAL) {
                    score += 10;
                }
            }
            if (matches.size() >= 5) {
                score += 26;
            } else if (matches.size() >= 4) {
                score += 14;
            }
            if (movesLeft <= 5) {
                score += matches.size() * 3;
            }
        }
        swap(rowA, colA, rowB, colB);
        return new Move(rowA, colA, rowB, colB, score);
    }

    private int getHintCellPriority(int row, int col) {
        int priority = 0;
        if (keys[row][col] > 0) {
            priority += 32;
        }
        if (countdownBomb[row][col] > 0) {
            // 提示优先救临近爆炸的炸弹，避免复杂后期关卡里错过关键步。
            priority += 28 + Math.max(0, 5 - countdownBomb[row][col]) * 8;
            if (isLastCountdownBombReady()) {
                // 只剩最后一个炸弹时，智能提示优先收尾拿护盾奖励。
                priority += 18;
            }
        }
        if (moveChest[row][col] > 0) {
            priority += 18;
        }
        if (musicBox[row][col] > 0) {
            // 音乐盒能产出可储备星弦琴，智能提示优先指向能开盒的走法。
            priority += 24;
        }
        if (hasRewardCell(row, col)) {
            priority += 14;
            if (isRewardCellMilestoneNear()) {
                // 只差1个奖励格拿罗盘时，智能提示优先指向可收奖励格的走法。
                priority += 20;
            }
        }
        return priority;
    }

    private boolean hasRewardCell(int row, int col) {
        return gift[row][col] > 0 || cloud[row][col] > 0 || gem[row][col] > 0
                || goldenEgg[row][col] > 0 || coinPouch[row][col] > 0 || paintBucket[row][col] > 0
                || windmill[row][col] > 0 || jewelBow[row][col] > 0 || stardustJar[row][col] > 0
                || wishLamp[row][col] > 0 || resonanceDrum[row][col] > 0 || auroraPrism[row][col] > 0
                || rainbowBottle[row][col] > 0 || energyPotion[row][col] > 0 || butterfly[row][col] > 0
                || portal[row][col] > 0 || hourglass[row][col] > 0 || luckyStar[row][col] > 0
                || luckyClover[row][col] > 0 || mysteryBox[row][col] > 0 || pearl[row][col] > 0
                || carousel[row][col] > 0 || ferrisTicket[row][col] > 0 || fireworksBarrel[row][col] > 0
                || starportBeacon[row][col] > 0 || meteorTrail[row][col] > 0 || rainbowArc[row][col] > 0
                || crystalCore[row][col] > 0 || musicBox[row][col] > 0;
    }

    private int getRewardCellCount() {
        int count = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (hasRewardCell(row, col)) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean hasAdjacentObstacle(int row, int col) {
        for (int nearRow = row - 1; nearRow <= row + 1; nearRow++) {
            for (int nearCol = col - 1; nearCol <= col + 1; nearCol++) {
                if (isInside(nearRow, nearCol) && (ice[nearRow][nearCol] > 0 || honey[nearRow][nearCol] > 0
                        || stone[nearRow][nearCol] > 0 || vine[nearRow][nearCol] > 0 || chain[nearRow][nearCol] > 0
                        || shell[nearRow][nearCol] > 0 || coralReef[nearRow][nearCol] > 0 || flower[nearRow][nearCol] > 0)) {
                    return true;
                }
            }
        }
        return false;
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
            idleHintCount++;
        }
    }

    private void clearHint() {
        hintRowA = NONE;
        hintColA = NONE;
        hintRowB = NONE;
        hintColB = NONE;
        hintUntilTime = 0;
    }

    private void refreshBoardActionTime() {
        lastBoardActionTime = System.currentTimeMillis();
        idleHintCount = 0;
        clearHint();
    }

    private void updateIdleHint() {
        if (levelComplete || levelFailed || showingLevelMap || showingSettings || activeProp != NONE) {
            return;
        }
        long now = System.currentTimeMillis();
        // 长时间未操作时逐步给出轻提示，提升新手和后期复杂关卡的可玩性。
        if (idleHintCount < 2 && now - lastBoardActionTime > 5200L + idleHintCount * 4200L
                && now > hintUntilTime) {
            showAvailableHint();
        }
    }

    private void checkLevelState() {
        if (levelComplete || levelFailed) {
            return;
        }

        Level level = levels.get(levelIndex);
        if (score >= level.targetScore && targetRemaining <= 0
                && iceRemaining <= 0 && honeyRemaining <= 0 && stoneRemaining <= 0 && vineRemaining <= 0
                && chainRemaining <= 0 && shellRemaining <= 0 && coralReefRemaining <= 0 && flowerRemaining <= 0 && keyRemaining <= 0
                && isMoveLimitGoalCleared(level) && isComboGoalCleared(level) && isScoreGoalCleared(level)) {
            levelComplete = true;
            lastBonusScore = movesLeft * 80;
            score += lastBonusScore;
            spawnWinFireworks();
            lastStars = movesLeft > level.moves / 2 ? 3 : (movesLeft > level.moves / 5 ? 2 : 1);
            challengeCleared = level.moveLimitGoal > 0;
            comboChallengeCleared = level.comboGoal > 0;
            scoreChallengeCleared = level.scoreGoal > 0;
            hiddenChallengeCleared = isHiddenChallengeCleared(level);
            lastRank = calculateLevelRank(level);
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
            recordLevelFailure();
            resetWinStreak();
        }
    }

    private void recordLevelFailure() {
        if (dailyChallengeMode || usedContinueThisLevel) {
            return;
        }

        // 连续失败会记录下来，下次开局给轻量补偿，减少卡关挫败感。
        levelFailStreaks[levelIndex] = Math.min(5, levelFailStreaks[levelIndex] + 1);
        prefs.edit().putInt(KEY_FAIL_STREAK_PREFIX + levelIndex, levelFailStreaks[levelIndex]).apply();
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

    private boolean isScoreGoalCleared(Level level) {
        return level.scoreGoal <= 0 || score >= level.scoreGoal;
    }

    private boolean isHiddenChallengeCleared(Level level) {
        return isHiddenChallengeLevel() && !usedContinueThisLevel && movesUsed <= Math.max(7, level.moves - 4);
    }

    private boolean isHiddenChallengeLevel() {
        return isHiddenChallengeLevel(levelIndex);
    }

    private boolean isHiddenChallengeLevel(int level) {
        return level >= 10 && level % 11 == 0;
    }

    private boolean isEliteLevel() {
        return isEliteLevel(levelIndex);
    }

    private boolean isEliteLevel(int level) {
        return levels.get(level).elite;
    }

    private int calculateLevelRank(Level level) {
        int rank = lastStars;
        if (score >= level.targetScore * 2) {
            rank++;
        }
        if (bestCombo >= 4) {
            rank++;
        }
        if (!usedContinueThisLevel && movesUsed <= Math.max(6, level.moves / 2)) {
            rank++;
        }
        if ((level.moveLimitGoal > 0 && challengeCleared)
                || (level.comboGoal > 0 && comboChallengeCleared)
                || (level.scoreGoal > 0 && scoreChallengeCleared)) {
            rank++;
        }
        if (hiddenChallengeCleared) {
            rank++;
        }
        return Math.min(6, rank);
    }

    private String buildRankText(int rank) {
        if (rank >= 6) {
            return "SSS";
        } else if (rank == 5) {
            return "SS";
        } else if (rank == 4) {
            return "S";
        } else if (rank == 3) {
            return "A";
        } else if (rank == 2) {
            return "B";
        } else if (rank == 1) {
            return "C";
        }
        return "-";
    }

    private void grantPerfectClearReward(Level level, boolean oldPerfectCleared) {
        if (dailyChallengeMode || usedContinueThisLevel || movesUsed > Math.max(6, level.moves / 2) || lastRank < 5) {
            return;
        }

        levelPerfectCleared[levelIndex] = true;
        if (oldPerfectCleared) {
            lastPerfectRetained = true;
            return;
        }
        // 完美通关奖励少步数、高评级的打法，给重玩提供更明确的冲刺目标。
        lastPerfectReward = 18 + lastRank * 4;
        coins += lastPerfectReward;
        lastPerfectRewardProp = lastRank >= 6 ? PROP_STAR_HARP : PROP_FIREWORK_CANNON;
        lastPerfectRewardAmount = 1;
        addReserveProp(lastPerfectRewardProp, lastPerfectRewardAmount);
    }

    private void saveDailyChallengeReward() {
        long today = getToday();
        long lastChallengeDay = prefs.getLong(KEY_DAILY_CHALLENGE_DAY, -1L);
        if (lastChallengeDay == today) {
            lastCoinReward = 0;
            lastDailyChallengeMilestoneProp = NONE;
            lastDailyChallengeMilestoneAmount = 0;
            return;
        }

        // 每日挑战独立奖励，不推进主线关卡进度。
        dailyChallengeStreak = lastChallengeDay == today - 1 ? dailyChallengeStreak + 1 : 1;
        lastCoinReward = 30 + lastStars * 10 + Math.min(5, dailyChallengeStreak - 1) * 6;
        coins += lastCoinReward;
        grantDailyChallengeMilestoneReward();
        prefs.edit()
                .putLong(KEY_DAILY_CHALLENGE_DAY, today)
                .putInt(KEY_DAILY_CHALLENGE_STREAK, dailyChallengeStreak)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void grantDailyChallengeMilestoneReward() {
        lastDailyChallengeMilestoneProp = NONE;
        lastDailyChallengeMilestoneAmount = 0;
        lastDailyChallengeMilestoneProp = getDailyChallengeMilestoneProp(dailyChallengeStreak);
        lastDailyChallengeMilestoneAmount = getDailyChallengeMilestoneAmount(lastDailyChallengeMilestoneProp);
        if (lastDailyChallengeMilestoneProp != NONE) {
            // 每日挑战连胜节点给稀有道具，强化持续回访动力。
            addReserveProp(lastDailyChallengeMilestoneProp, lastDailyChallengeMilestoneAmount);
        }
    }

    private int getDailyChallengeMilestoneProp(int streak) {
        if (streak == 3) {
            return PROP_ROCKET;
        } else if (streak == 7) {
            return PROP_AURORA_ORB;
        } else if (streak == 14) {
            return PROP_MOON_TICKET;
        } else if (streak == 21) {
            return PROP_BUBBLE_WAND;
        } else if (streak == 45) {
            return PROP_STAR_HARP;
        } else if (streak > 0 && streak % 30 == 0) {
            return PROP_SNOW_GLOBE;
        }
        return NONE;
    }

    private int getDailyChallengeMilestoneAmount(int prop) {
        if (prop == PROP_MOON_TICKET || prop == PROP_SNOW_GLOBE) {
            return 2;
        }
        return prop == NONE ? 0 : 1;
    }

    private void updateDailyGoalProgress() {
        if (dailyChallengeMode || dailyGoalClaimed) {
            return;
        }

        // 每日目标按通关星数累积，给主线和补星都提供轻量回访奖励。
        dailyGoalProgress = Math.min(6, dailyGoalProgress + lastStars);
        prefs.edit()
                .putLong(KEY_DAILY_GOAL_DAY, getToday())
                .putInt(KEY_DAILY_GOAL_PROGRESS, dailyGoalProgress)
                .apply();
    }

    private void claimDailyGoalReward() {
        if (dailyGoalClaimed || dailyGoalProgress < 6) {
            lastChestReward = 0;
            lastRankChestReward = 0;
            lastChestRewardProp = NONE;
            lastChestRewardAmount = 0;
            lastChapterChestReward = 0;
            lastDailyGoalReward = 0;
            lastChestNoticeType = 4;
            chestNoticeUntilTime = System.currentTimeMillis() + 1400;
            return;
        }

        lastDailyGoalReward = 35 + Math.min(15, dailyStreak * 2);
        lastChestReward = 0;
        lastRankChestReward = 0;
        lastChestRewardProp = NONE;
        lastChestRewardAmount = 0;
        lastChapterChestReward = 0;
        coins += lastDailyGoalReward;
        // 每日目标奖励进入长期储备，确保回到关卡后仍能使用。
        lastChestRewardProp = getDailyGoalRewardProp();
        lastChestRewardAmount = getDailyGoalRewardAmount(lastChestRewardProp);
        addReserveProp(lastChestRewardProp, lastChestRewardAmount);
        dailyGoalClaimed = true;
        prefs.edit()
                .putLong(KEY_DAILY_GOAL_DAY, getToday())
                .putInt(KEY_DAILY_GOAL_PROGRESS, dailyGoalProgress)
                .putBoolean(KEY_DAILY_GOAL_CLAIMED, true)
                .putInt(KEY_COINS, coins)
                .apply();
        chestNoticeUntilTime = System.currentTimeMillis() + 1800;
        playHaptic(HapticFeedbackConstants.CONFIRM);
        playSuccessTone();
    }

    private int getDailyGoalRewardProp() {
        int streak = Math.max(1, dailyStreak);
        if (streak >= 21) {
            return PROP_SNOW_GLOBE;
        } else if (streak >= 14) {
            return PROP_BUBBLE_WAND;
        } else if (streak >= 7) {
            return PROP_STAR_COMPASS;
        }
        return PROP_MOON_TICKET;
    }

    private int getDailyGoalRewardAmount(int prop) {
        return prop == PROP_SNOW_GLOBE ? 2 : 1;
    }

    private void grantWinStreakReward() {
        winStreak++;
        lastWinStreakReward = winStreak >= 3 ? Math.min(60, winStreak * 5) : 0;
        coins += lastWinStreakReward;
        grantWinStreakPropReward();
        prefs.edit()
                .putInt(KEY_WIN_STREAK, winStreak)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void grantWinStreakPropReward() {
        lastWinStreakRewardProp = NONE;
        lastWinStreakRewardAmount = 0;
        lastWinStreakRewardProp = getWinStreakMilestoneProp(winStreak);
        lastWinStreakRewardAmount = getWinStreakMilestoneAmount(winStreak, lastWinStreakRewardProp);
        if (lastWinStreakRewardProp != NONE) {
            // 连胜节点补给稀有道具，鼓励玩家连续冲关和反复挑战。
            addReserveProp(lastWinStreakRewardProp, lastWinStreakRewardAmount);
        }
    }

    private void resetWinStreak() {
        if (dailyChallengeMode || winStreak <= 0) {
            return;
        }

        winStreak = 0;
        prefs.edit().putInt(KEY_WIN_STREAK, winStreak).apply();
    }

    private String buildNextWinStreakRewardHint() {
        int nextStreak = getNextWinStreakMilestone();
        int prop = getWinStreakMilestoneProp(nextStreak);
        return prop == NONE ? "" : " 到" + nextStreak + "奖" + getPropName(prop)
                + "+" + getWinStreakMilestoneAmount(nextStreak, prop);
    }

    private int getNextWinStreakMilestone() {
        for (int streak = winStreak + 1; streak <= winStreak + 15; streak++) {
            if (getWinStreakMilestoneProp(streak) != NONE) {
                return streak;
            }
        }
        return NONE;
    }

    private int getWinStreakMilestoneProp(int streak) {
        if (streak == 5) {
            return PROP_STAR_COMPASS;
        } else if (streak == 10) {
            return PROP_FIREWORK_CANNON;
        } else if (streak == 12) {
            return PROP_BUBBLE_WAND;
        } else if (streak == 20) {
            return PROP_STAR_HARP;
        } else if (streak == 25 || (streak > 0 && streak % 15 == 0)) {
            return PROP_SNOW_GLOBE;
        }
        return NONE;
    }

    private int getWinStreakMilestoneAmount(int streak, int prop) {
        if (prop == NONE) {
            return 0;
        } else if (streak == 10 || (streak > 0 && streak % 15 == 0)) {
            return 2;
        } else if (streak == 25) {
            return 3;
        }
        return 1;
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
        checkAchievement(6, getTotalRankScore() >= 180, 150);
        checkAchievement(7, lastRank >= 6, 120);
        checkAchievement(8, hiddenChallengeCleared, 100);
        checkAchievement(9, lastGemReward > 0 || lastCoinPouchReward > 0 || lastWishLampReward > 0
                || lastResonanceDrumReward > 0 || lastAuroraPrismReward > 0 || lastPortalReward > 0
                || lastLuckyStarRewardProp != NONE || lastLuckyCloverRewardType > 0 || lastMysteryRewardType > 0
                || lastMusicBoxReward > 0, 90);
        checkAchievement(10, getTotalStars() >= 300, 220);
        checkAchievement(11, getTotalRankScore() >= 420, 240);
        checkAchievement(12, getFullyClearedChapterCount() >= 3, 260);
        checkAchievement(13, getTotalClearedEliteCount() >= 8, 220);
        // 后期成就继续覆盖终章、总星数和高评级，给满级玩家更长的追求线。
        checkAchievement(14, getChapterStars(CHAPTER_COUNT - 1) >= 45, 280);
        checkAchievement(15, getTotalStars() >= 420, 320);
        checkAchievement(16, getTotalRankScore() >= 700, 360);
        checkAchievement(17, getFullyClearedChapterCount() >= CHAPTER_COUNT, 420);
        // 扩展到 360 关后补充更长线的星数、评级和新章节追求。
        checkAchievement(18, highestUnlockedLevel >= 260, 360);
        checkAchievement(19, getTotalStars() >= 600, 460);
        checkAchievement(20, getTotalRankScore() >= 1000, 520);
        checkAchievement(21, getChapterStars(CHAPTER_COUNT - 2) >= 54, 420);
        checkAchievement(22, getChapterStars(CHAPTER_COUNT - 1) >= 54, 520);
        checkAchievement(23, getTotalClearedEliteCount() >= 16, 560);
        checkAchievement(24, getTotalStars() >= 780, 620);
        checkAchievement(25, getTotalRankScore() >= 1300, 680);
        checkAchievement(26, highestUnlockedLevel >= 300, 620);
        checkAchievement(27, getTotalStars() >= 900, 760);
        checkAchievement(28, getTotalRankScore() >= 1600, 820);
        checkAchievement(29, getFullyClearedChapterCount() >= CHAPTER_COUNT, 900);
        checkAchievement(30, highestUnlockedLevel >= 340, 760);
        checkAchievement(31, getTotalStars() >= 1020, 920);
        checkAchievement(32, getTotalRankScore() >= 1900, 980);
        checkAchievement(33, getTotalClearedEliteCount() >= 22, 1040);
        checkAchievement(34, highestUnlockedLevel >= 380, 920);
        checkAchievement(35, getTotalStars() >= 1140, 1080);
        checkAchievement(36, getTotalRankScore() >= 2200, 1120);
        checkAchievement(37, getFullyClearedChapterCount() >= CHAPTER_COUNT, 1200);
        // 400关版本追加终局星数与评级成就，给满级玩家继续刷SSS的目标。
        checkAchievement(38, getTotalStars() >= 1200, 1260);
        checkAchievement(39, getTotalRankScore() >= 2400, 1320);
    }

    private void checkAchievement(int index, boolean reached, int reward) {
        if (achievementsClaimed[index] || !reached) {
            return;
        }

        // 成就奖励把长期目标转成可见金币反馈。
        achievementsClaimed[index] = true;
        lastAchievementReward += reward;
        coins += reward;
        grantAchievementPropReward(index);
        prefs.edit()
                .putBoolean(KEY_ACHIEVEMENT_PREFIX + index, true)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void grantAchievementPropReward(int index) {
        int prop = getAchievementRewardProp(index);
        int amount = getAchievementRewardAmount(index);
        if (prop == NONE) {
            return;
        }

        // 高阶成就除金币外给稀有道具，延长满星和评级后的追求。
        addReserveProp(prop, amount);
        lastAchievementRewardProp = prop;
        lastAchievementRewardAmount += amount;
    }

    private int getAchievementRewardProp(int index) {
        if (index == 7 || index == 13) {
            return PROP_MOON_TICKET;
        } else if (index == 16 || index == 20) {
            return PROP_AURORA_ORB;
        } else if (index == 17 || index == 21 || index == 22) {
            return PROP_STAR_COMPASS;
        } else if (index == 23) {
            return PROP_FIREWORK_CANNON;
        } else if (index == 24 || index == 28) {
            return PROP_STAR_COMPASS;
        } else if (index == 25 || index == 29 || index == 33) {
            return PROP_BUBBLE_WAND;
        } else if (index == 26 || index == 27 || index == 30) {
            return PROP_SNOW_GLOBE;
        } else if (index == 31) {
            return PROP_STAR_COMPASS;
        } else if (index == 32 || index == 34 || index == 36) {
            return PROP_STAR_HARP;
        } else if (index == 35 || index == 37 || index == 38) {
            return PROP_SNOW_GLOBE;
        } else if (index == 39) {
            return PROP_STAR_HARP;
        }
        return NONE;
    }

    private int getAchievementRewardAmount(int index) {
        if (getAchievementRewardProp(index) == NONE) {
            return 0;
        } else if (index == 39) {
            return 3;
        } else if (index >= 23) {
            return 2;
        }
        return 1;
    }

    private void loadProgress() {
        highestUnlockedLevel = Math.min(prefs.getInt(KEY_UNLOCKED_LEVEL, 0), levels.size() - 1);
        coins = prefs.getInt(KEY_COINS, 30);
        starChestClaimed = prefs.getInt(KEY_STAR_CHEST_CLAIMED, 0);
        rankChestClaimed = prefs.getInt(KEY_RANK_CHEST_CLAIMED, 0);
        winStreak = prefs.getInt(KEY_WIN_STREAK, 0);
        dailyChallengeStreak = prefs.getInt(KEY_DAILY_CHALLENGE_STREAK, 0);
        seasonLevels = prefs.getInt(KEY_SEASON_LEVELS, 0);
        seasonStars = prefs.getInt(KEY_SEASON_STARS, 0);
        seasonRewardStep = prefs.getInt(KEY_SEASON_REWARD_STEP, 0);
        soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true);
        hapticEnabled = prefs.getBoolean(KEY_HAPTIC_ENABLED, true);
        for (int prop = 0; prop < PROP_COUNT; prop++) {
            propReserve[prop] = prefs.getInt(KEY_PROP_RESERVE_PREFIX + prop, 0);
        }
        grantDailyReward();
        loadDailyGoal();
        for (int i = 0; i < levels.size(); i++) {
            levelStars[i] = prefs.getInt(KEY_STARS_PREFIX + i, 0);
            levelBestScores[i] = prefs.getInt(KEY_BEST_SCORE_PREFIX + i, 0);
            levelRanks[i] = prefs.getInt(KEY_RANK_PREFIX + i, 0);
            levelFailStreaks[i] = prefs.getInt(KEY_FAIL_STREAK_PREFIX + i, 0);
            levelHiddenChallengesCleared[i] = prefs.getBoolean(KEY_HIDDEN_CHALLENGE_PREFIX + i,
                    isHiddenChallengeLevel(i) && levelRanks[i] >= 4);
            levelPerfectCleared[i] = prefs.getBoolean(KEY_PERFECT_CLEAR_PREFIX + i, false);
        }
        for (int i = 0; i < chapterChestClaimed.length; i++) {
            chapterChestClaimed[i] = prefs.getBoolean(KEY_CHAPTER_CHEST_PREFIX + i, false);
            chapterMasteryClaimed[i] = prefs.getBoolean(KEY_CHAPTER_MASTERY_PREFIX + i, false);
            chapterEliteClaimed[i] = prefs.getBoolean(KEY_CHAPTER_ELITE_PREFIX + i, false);
            chapterRankClaimed[i] = prefs.getBoolean(KEY_CHAPTER_RANK_PREFIX + i, false);
            chapterHiddenClaimed[i] = prefs.getBoolean(KEY_CHAPTER_HIDDEN_PREFIX + i, false);
            chapterPerfectClaimed[i] = prefs.getBoolean(KEY_CHAPTER_PERFECT_PREFIX + i, false);
        }
        for (int i = 0; i < achievementsClaimed.length; i++) {
            achievementsClaimed[i] = prefs.getBoolean(KEY_ACHIEVEMENT_PREFIX + i, false);
        }
    }

    private void saveLevelProgress() {
        Level level = levels.get(levelIndex);
        int oldStars = levelStars[levelIndex];
        int oldRank = levelRanks[levelIndex];
        boolean oldHiddenCleared = levelHiddenChallengesCleared[levelIndex];
        boolean oldPerfectCleared = levelPerfectCleared[levelIndex];
        levelStars[levelIndex] = Math.max(levelStars[levelIndex], lastStars);
        levelBestScores[levelIndex] = Math.max(levelBestScores[levelIndex], score);
        levelRanks[levelIndex] = Math.max(levelRanks[levelIndex], lastRank);
        highestUnlockedLevel = Math.max(highestUnlockedLevel, Math.min(levelIndex + 1, levels.size() - 1));
        if (levelStars[levelIndex] > oldStars) {
            // 重玩补星给额外金币，鼓励把老关卡刷到满星。
            lastStarUpgradeReward = (levelStars[levelIndex] - oldStars) * 12;
            coins += lastStarUpgradeReward;
        }
        if (oldStars < 3 && levelStars[levelIndex] >= 3) {
            grantFullStarReward(level);
        }
        if (levelRanks[levelIndex] > oldRank) {
            // 评级奖励鼓励玩家追求高分、连击和挑战目标。
            lastRankUpgradeReward = (levelRanks[levelIndex] - oldRank) * 8;
            coins += lastRankUpgradeReward;
            grantRankUpgradePropReward(oldRank);
        }
        grantReplayImprovementReward(oldStars, oldRank);
        if (hiddenChallengeCleared && !oldHiddenCleared) {
            // 隐藏挑战奖励只在首次达成时发放，避免回访重复刷奖励。
            levelHiddenChallengesCleared[levelIndex] = true;
            lastHiddenReward = 20;
            coins += lastHiddenReward;
            lastHiddenRewardProp = PROP_BOMB;
            lastHiddenRewardAmount = 1;
            addReserveProp(lastHiddenRewardProp, lastHiddenRewardAmount);
        }
        grantFirstClearReward(level, oldStars);
        grantEliteLevelReward(level);
        grantPerfectClearReward(level, oldPerfectCleared);
        grantAchievementRewards();
        updateSeasonQuestProgress();
        grantChapterEliteReward();
        grantChapterRankReward();
        grantChapterHiddenReward();
        grantChapterPerfectReward();
        grantChapterMasteryReward();
        updateDailyGoalProgress();
        prefs.edit()
                .putInt(KEY_UNLOCKED_LEVEL, highestUnlockedLevel)
                .putInt(KEY_STARS_PREFIX + levelIndex, levelStars[levelIndex])
                .putInt(KEY_BEST_SCORE_PREFIX + levelIndex, levelBestScores[levelIndex])
                .putInt(KEY_RANK_PREFIX + levelIndex, levelRanks[levelIndex])
                .putBoolean(KEY_HIDDEN_CHALLENGE_PREFIX + levelIndex, levelHiddenChallengesCleared[levelIndex])
                .putBoolean(KEY_PERFECT_CLEAR_PREFIX + levelIndex, levelPerfectCleared[levelIndex])
                .putInt(KEY_FAIL_STREAK_PREFIX + levelIndex, 0)
                .putInt(KEY_COINS, coins)
                .putInt(KEY_STAR_CHEST_CLAIMED, starChestClaimed)
                .putInt(KEY_RANK_CHEST_CLAIMED, rankChestClaimed)
                .apply();
        levelFailStreaks[levelIndex] = 0;
    }

    private void grantFullStarReward(Level level) {
        // 首次满星额外给奖励，把补星目标转成更明确的正反馈。
        lastFullStarReward = 24 + getChapterIndex(levelIndex) * 4 + (level.elite ? 12 : 0);
        coins += lastFullStarReward;
        addReserveProp(PROP_CLEANSE, 1);
    }

    private void grantFirstClearReward(Level level, int oldStars) {
        if (oldStars > 0) {
            return;
        }

        // 首次通关奖励强化主线推进感，和重玩补星奖励区分开。
        lastFirstClearReward = 18 + Math.min(60, levelIndex / 2) + (level.elite ? 18 : 0);
        coins += lastFirstClearReward;
    }

    private void grantReplayImprovementReward(int oldStars, int oldRank) {
        if (oldStars <= 0 || (levelStars[levelIndex] <= oldStars && levelRanks[levelIndex] <= oldRank)) {
            return;
        }

        // 老关卡补星或冲评级额外给一次回访奖励，让推荐重玩有更明确的收益。
        int starGain = Math.max(0, levelStars[levelIndex] - oldStars);
        int rankGain = Math.max(0, levelRanks[levelIndex] - oldRank);
        lastReplayReward = 12 + starGain * 18 + rankGain * 10;
        coins += lastReplayReward;
        if (levelStars[levelIndex] >= 3 && oldStars < 3) {
            lastReplayRewardProp = PROP_STAR_HARP;
            lastReplayRewardAmount = 1;
        } else if (levelRanks[levelIndex] >= 4 && oldRank < 4) {
            lastReplayRewardProp = PROP_STAR_COMPASS;
            lastReplayRewardAmount = 1;
        }
        if (lastReplayRewardProp != NONE) {
            addReserveProp(lastReplayRewardProp, lastReplayRewardAmount);
        }
    }

    private void grantRankUpgradePropReward(int oldRank) {
        int rank = levelRanks[levelIndex];
        if (oldRank < 6 && rank >= 6) {
            lastRankUpgradeRewardProp = PROP_STAR_HARP;
            lastRankUpgradeRewardAmount = 1;
        } else if (oldRank < 5 && rank >= 5) {
            lastRankUpgradeRewardProp = PROP_FIREWORK_CANNON;
            lastRankUpgradeRewardAmount = 1;
        } else if (oldRank < 4 && rank >= 4) {
            lastRankUpgradeRewardProp = PROP_STAR_COMPASS;
            lastRankUpgradeRewardAmount = 1;
        }
        if (lastRankUpgradeRewardProp != NONE) {
            // 首次冲到高评级时给稀有道具，把S/SS/SSS变成更明确的回访目标。
            addReserveProp(lastRankUpgradeRewardProp, lastRankUpgradeRewardAmount);
        }
    }

    private void updateSeasonQuestProgress() {
        if (dailyChallengeMode) {
            return;
        }

        // 赛季任务记录持续通关和补星成果，给长线玩家稳定的阶段奖励。
        seasonLevels++;
        seasonStars += lastStars;
        int nextStep = seasonRewardStep + 1;
        int levelTarget = nextStep * 8;
        int starTarget = nextStep * 22;
        if (seasonLevels < levelTarget && seasonStars < starTarget) {
            saveSeasonProgress();
            return;
        }

        seasonRewardStep = nextStep;
        lastSeasonReward = 70 + seasonRewardStep * 18;
        coins += lastSeasonReward;
        grantSeasonPropReward(seasonRewardStep);
        saveSeasonProgress();
    }

    private void grantSeasonPropReward(int step) {
        lastSeasonRewardProp = NONE;
        lastSeasonRewardAmount = 0;
        lastSeasonRewardProp = getSeasonRewardProp(step);
        lastSeasonRewardAmount = getSeasonRewardAmount(lastSeasonRewardProp);
        if (lastSeasonRewardProp != NONE) {
            addReserveProp(lastSeasonRewardProp, lastSeasonRewardAmount);
        }
    }

    private int getSeasonRewardProp(int step) {
        if (step % 10 == 0) {
            return PROP_STAR_HARP;
        } else if (step % 8 == 0) {
            return PROP_SNOW_GLOBE;
        } else if (step % 6 == 0) {
            return PROP_BUBBLE_WAND;
        } else if (step % 4 == 0) {
            return PROP_STAR_COMPASS;
        } else if (step % 3 == 0) {
            return PROP_FIREWORK_CANNON;
        } else if (step % 2 == 0) {
            return PROP_AURORA_ORB;
        }
        return NONE;
    }

    private int getSeasonRewardAmount(int prop) {
        if (prop == PROP_STAR_HARP || prop == PROP_SNOW_GLOBE) {
            return 2;
        }
        return prop == NONE ? 0 : 1;
    }

    private void saveSeasonProgress() {
        prefs.edit()
                .putInt(KEY_SEASON_LEVELS, seasonLevels)
                .putInt(KEY_SEASON_STARS, seasonStars)
                .putInt(KEY_SEASON_REWARD_STEP, seasonRewardStep)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void grantEliteLevelReward(Level level) {
        if (dailyChallengeMode || !level.elite) {
            return;
        }

        // 精英关固定给额外金币，让阶段性难关更有通关价值。
        lastEliteReward = 26 + lastStars * 6 + getChapterIndex(levelIndex) * 4;
        coins += lastEliteReward;
    }

    private void grantChapterMasteryReward() {
        int chapter = getChapterIndex(levelIndex);
        if (chapterMasteryClaimed[chapter] || getChapterStars(chapter) < CHAPTER_SIZE * 3) {
            return;
        }

        // 章节满星奖励把补星追求转成一次明确的高价值回报。
        chapterMasteryClaimed[chapter] = true;
        lastChapterMasteryReward = 120 + chapter * 30;
        coins += lastChapterMasteryReward;
        addReserveProp(PROP_CLEANSE, 1);
        if (isFireworksChapter(chapter)) {
            addReserveProp(PROP_FIREWORK_CANNON, 1);
        } else if (isRainbowValleyChapter(chapter)) {
            addReserveProp(PROP_AURORA_ORB, 1);
        } else if (isCrystalTowerChapter(chapter)) {
            addReserveProp(PROP_STAR_COMPASS, 1);
        } else if (isBubbleGalaxyChapter(chapter)) {
            addReserveProp(PROP_AURORA_ORB, 1);
            addReserveProp(PROP_STAR_COMPASS, 1);
            addReserveProp(PROP_BUBBLE_WAND, 1);
        } else if (isMintFireworksChapter(chapter)) {
            addReserveProp(PROP_FIREWORK_CANNON, 1);
            addReserveProp(PROP_CLEANSE, 1);
        } else if (isFrostCandyChapter(chapter)) {
            addReserveProp(PROP_STAR_COMPASS, 1);
            addReserveProp(PROP_AURORA_ORB, 1);
        } else if (isGlassCloudChapter(chapter)) {
            addReserveProp(PROP_BUBBLE_WAND, 1);
            addReserveProp(PROP_FIREWORK_CANNON, 1);
        } else if (isHoneyClockChapter(chapter)) {
            addReserveProp(PROP_CLOCK, 1);
            addReserveProp(PROP_SNOW_GLOBE, 1);
        } else if (isPrismSongChapter(chapter)) {
            addReserveProp(PROP_AURORA_ORB, 1);
            addReserveProp(PROP_STAR_COMPASS, 1);
            addReserveProp(PROP_STAR_HARP, 1);
        } else if (isStarHarpStageChapter(chapter)) {
            addReserveProp(PROP_STAR_HARP, 2);
            addReserveProp(PROP_AURORA_ORB, 1);
        } else if (isCandyTheaterChapter(chapter)) {
            addReserveProp(PROP_STAR_HARP, 1);
            addReserveProp(PROP_SNOW_GLOBE, 1);
            addReserveProp(PROP_FIREWORK_CANNON, 1);
        }
        prefs.edit()
                .putBoolean(KEY_CHAPTER_MASTERY_PREFIX + chapter, true)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void grantChapterEliteReward() {
        int chapter = getChapterIndex(levelIndex);
        if (chapterEliteClaimed[chapter] || getChapterEliteCount(chapter) <= 0
                || getChapterClearedEliteCount(chapter) < getChapterEliteCount(chapter)) {
            return;
        }

        // 章节内精英关全清后给一次阶段奖励，让高难节点有额外追求。
        chapterEliteClaimed[chapter] = true;
        lastChapterEliteReward = 90 + chapter * 24;
        coins += lastChapterEliteReward;
        addReserveProp(PROP_METEOR, 1);
        if (isFireworksChapter(chapter)) {
            addReserveProp(PROP_FIREWORK_CANNON, 1);
        } else if (isRainbowValleyChapter(chapter)) {
            addReserveProp(PROP_STAR_COMPASS, 1);
        } else if (isCrystalTowerChapter(chapter)) {
            addReserveProp(PROP_FIREWORK_CANNON, 1);
        } else if (isBubbleGalaxyChapter(chapter)) {
            addReserveProp(PROP_AURORA_ORB, 1);
            addReserveProp(PROP_BUBBLE_WAND, 1);
        } else if (isMintFireworksChapter(chapter)) {
            addReserveProp(PROP_FIREWORK_CANNON, 1);
            addReserveProp(PROP_STAR_COMPASS, 1);
        } else if (isFrostCandyChapter(chapter)) {
            addReserveProp(PROP_STAR_COMPASS, 1);
            addReserveProp(PROP_BUBBLE_WAND, 1);
        } else if (isGlassCloudChapter(chapter)) {
            addReserveProp(PROP_FIREWORK_CANNON, 1);
            addReserveProp(PROP_AURORA_ORB, 1);
        } else if (isHoneyClockChapter(chapter)) {
            addReserveProp(PROP_CLOCK, 1);
            addReserveProp(PROP_FIREWORK_CANNON, 1);
        } else if (isPrismSongChapter(chapter)) {
            addReserveProp(PROP_AURORA_ORB, 1);
            addReserveProp(PROP_STAR_HARP, 1);
        } else if (isStarHarpStageChapter(chapter)) {
            addReserveProp(PROP_STAR_HARP, 1);
            addReserveProp(PROP_STAR_COMPASS, 1);
        } else if (isCandyTheaterChapter(chapter)) {
            addReserveProp(PROP_STAR_HARP, 1);
            addReserveProp(PROP_BUBBLE_WAND, 1);
        }
        prefs.edit()
                .putBoolean(KEY_CHAPTER_ELITE_PREFIX + chapter, true)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void grantChapterRankReward() {
        int chapter = getChapterIndex(levelIndex);
        int rankTarget = getChapterRankRewardTarget();
        if (chapterRankClaimed[chapter] || getChapterRankScore(chapter) < rankTarget) {
            return;
        }

        // 章节评级奖励鼓励反复冲高分，把评级进度转成一次强力补给。
        chapterRankClaimed[chapter] = true;
        lastChapterRankReward = 100 + chapter * 26;
        coins += lastChapterRankReward;
        addReserveProp(PROP_TIDE, 1);
        if (isFireworksChapter(chapter)) {
            addReserveProp(PROP_FIREWORK_CANNON, 1);
            addReserveProp(PROP_STAR_COMPASS, 1);
        } else if (isRainbowValleyChapter(chapter)) {
            addReserveProp(PROP_AURORA_ORB, 1);
            addReserveProp(PROP_STAR_COMPASS, 1);
        } else if (isCrystalTowerChapter(chapter)) {
            addReserveProp(PROP_FIREWORK_CANNON, 1);
            addReserveProp(PROP_STAR_COMPASS, 1);
        } else if (isBubbleGalaxyChapter(chapter)) {
            addReserveProp(PROP_AURORA_ORB, 1);
            addReserveProp(PROP_STAR_COMPASS, 1);
            addReserveProp(PROP_BUBBLE_WAND, 1);
        } else if (isMintFireworksChapter(chapter)) {
            addReserveProp(PROP_FIREWORK_CANNON, 1);
            addReserveProp(PROP_STAR_COMPASS, 1);
        } else if (isFrostCandyChapter(chapter)) {
            addReserveProp(PROP_STAR_COMPASS, 2);
            addReserveProp(PROP_BUBBLE_WAND, 1);
        } else if (isGlassCloudChapter(chapter)) {
            addReserveProp(PROP_FIREWORK_CANNON, 1);
            addReserveProp(PROP_AURORA_ORB, 1);
            addReserveProp(PROP_BUBBLE_WAND, 1);
        } else if (isHoneyClockChapter(chapter)) {
            addReserveProp(PROP_CLOCK, 2);
            addReserveProp(PROP_SNOW_GLOBE, 1);
        } else if (isPrismSongChapter(chapter)) {
            addReserveProp(PROP_AURORA_ORB, 1);
            addReserveProp(PROP_STAR_COMPASS, 1);
            addReserveProp(PROP_STAR_HARP, 1);
        } else if (isStarHarpStageChapter(chapter)) {
            addReserveProp(PROP_STAR_HARP, 2);
            addReserveProp(PROP_STAR_COMPASS, 1);
        } else if (isCandyTheaterChapter(chapter)) {
            addReserveProp(PROP_STAR_HARP, 1);
            addReserveProp(PROP_SNOW_GLOBE, 1);
            addReserveProp(PROP_BUBBLE_WAND, 1);
        }
        prefs.edit()
                .putBoolean(KEY_CHAPTER_RANK_PREFIX + chapter, true)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void grantChapterHiddenReward() {
        int chapter = getChapterIndex(levelIndex);
        int hiddenCount = getChapterHiddenChallengeCount(chapter);
        if (chapterHiddenClaimed[chapter] || hiddenCount <= 0
                || getChapterClearedHiddenChallengeCount(chapter) < hiddenCount) {
            return;
        }

        // 全章隐藏挑战完成后给一次额外回访奖励，强化老关补挑战的价值。
        chapterHiddenClaimed[chapter] = true;
        lastChapterHiddenReward = 80 + chapter * 18;
        coins += lastChapterHiddenReward;
        addReserveProp(PROP_CLOCK, 1);
        addReserveProp(PROP_STAR_COMPASS, 1);
        prefs.edit()
                .putBoolean(KEY_CHAPTER_HIDDEN_PREFIX + chapter, true)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void grantChapterPerfectReward() {
        int chapter = getChapterIndex(levelIndex);
        if (chapterPerfectClaimed[chapter] || getChapterUnlockedCount(chapter) < CHAPTER_SIZE
                || getChapterPerfectClearCount(chapter) < getChapterUnlockedCount(chapter)) {
            return;
        }

        // 全章完美通关给高手向一次性奖励，让极限回访也有明确回报。
        chapterPerfectClaimed[chapter] = true;
        lastChapterPerfectReward = 140 + chapter * 28;
        coins += lastChapterPerfectReward;
        addReserveProp(PROP_STAR_HARP, 1);
        addReserveProp(PROP_STAR_COMPASS, 1);
        prefs.edit()
                .putBoolean(KEY_CHAPTER_PERFECT_PREFIX + chapter, true)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private boolean isFireworksChapter(int chapter) {
        return chapterNames[chapter].equals("烟花星港");
    }

    private boolean isRainbowValleyChapter(int chapter) {
        return chapterNames[chapter].equals("流星彩虹谷");
    }

    private boolean isCrystalTowerChapter(int chapter) {
        return chapterNames[chapter].equals("奇迹糖晶塔");
    }

    private boolean isBubbleGalaxyChapter(int chapter) {
        return chapterNames[chapter].equals("泡泡星河岛");
    }

    private boolean isMintFireworksChapter(int chapter) {
        return chapterNames[chapter].equals("花火薄荷城");
    }

    private boolean isFrostCandyChapter(int chapter) {
        return chapterNames[chapter].equals("星霜糖果港");
    }

    private boolean isGlassCloudChapter(int chapter) {
        return chapterNames[chapter].equals("彩云琉璃境");
    }

    private boolean isHoneyClockChapter(int chapter) {
        return chapterNames[chapter].equals("蜜星钟楼");
    }

    private boolean isPrismSongChapter(int chapter) {
        return chapterNames[chapter].equals("极彩乐章谷");
    }

    private boolean isStarHarpStageChapter(int chapter) {
        return chapterNames[chapter].equals("星弦幻音台");
    }

    private boolean isCandyTheaterChapter(int chapter) {
        return chapterNames[chapter].equals("糖彩乐园剧场");
    }

    private int getChapterRankRewardTarget() {
        return CHAPTER_SIZE * 6 * 3 / 4;
    }

    private void applyChapterMasteryStarterPerks() {
        int mastered = getClaimedChapterMasteryCount();
        if (mastered <= 0) {
            return;
        }

        // 章节大师奖励会转成后续关卡的固定开局助力。
        addProp(PROP_MAGIC_WAND, 1);
        if (mastered >= 2) {
            addProp(PROP_BRUSH, 1);
        }
        if (mastered >= 4) {
            addProp(PROP_COLOR_BLAST, 1);
        }
    }

    private void applyComebackAssist() {
        int failStreak = levelFailStreaks[levelIndex];
        if (dailyChallengeMode || failStreak <= 0) {
            return;
        }

        // 卡关后下次开局逐步补一点步数和定向道具，帮助玩家继续推进。
        lastComebackAssistMoves = Math.min(3, failStreak);
        movesLeft += lastComebackAssistMoves;
        moveLimitBonus += lastComebackAssistMoves;
        if (failStreak >= 2) {
            lastComebackAssistProp = failStreak >= 4 ? PROP_STAR_HARP : PROP_BOMB;
            lastComebackAssistAmount = 1;
            addProp(lastComebackAssistProp, lastComebackAssistAmount);
        }
    }

    private String buildComebackAssistPreviewText() {
        int failStreak = levelFailStreaks[levelIndex];
        if (dailyChallengeMode || failStreak <= 0) {
            return "";
        }

        // 失败结算提前展示下局助力，降低反复尝试时的不确定感。
        String text = "下局助力 +" + Math.min(3, failStreak) + "步";
        if (failStreak >= 2) {
            text += " " + getPropName(failStreak >= 4 ? PROP_STAR_HARP : PROP_BOMB) + "+1";
        }
        return text;
    }

    private int getClaimedChapterMasteryCount() {
        int count = 0;
        for (boolean claimed : chapterMasteryClaimed) {
            if (claimed) {
                count++;
            }
        }
        return count;
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
            lastDailyRewardProp = NONE;
            lastDailyRewardPropAmount = 0;
            return;
        }

        // 连续登录越久，金币补给越高，推动长期回访。
        dailyStreak = lastRewardDay == today - 1 ? dailyStreak + 1 : 1;
        dailyRewardAmount = 20 + Math.min(6, dailyStreak - 1) * 5;
        coins += dailyRewardAmount;
        grantDailyLoginPropReward();
        prefs.edit()
                .putLong(KEY_DAILY_REWARD_DAY, today)
                .putInt(KEY_DAILY_STREAK, dailyStreak)
                .putInt(KEY_COINS, coins)
                .apply();
    }

    private void grantDailyLoginPropReward() {
        lastDailyRewardProp = NONE;
        lastDailyRewardPropAmount = 0;
        lastDailyRewardProp = getDailyLoginRewardProp(dailyStreak);
        lastDailyRewardPropAmount = getDailyLoginRewardAmount(lastDailyRewardProp);
        if (lastDailyRewardProp != NONE) {
            // 连签节点送稀有道具，给每日回访一个更明确的期待。
            addReserveProp(lastDailyRewardProp, lastDailyRewardPropAmount);
        }
    }

    private String buildNextDailyLoginRewardHint() {
        int nextStreak = getNextDailyLoginMilestone();
        int prop = getDailyLoginRewardProp(nextStreak);
        return prop == NONE ? "" : " 到" + nextStreak + "奖" + getPropName(prop)
                + "+" + getDailyLoginRewardAmount(prop);
    }

    private int getNextDailyLoginMilestone() {
        // 预告最近的连签奖励节点，让长期登录目标更清楚。
        for (int streak = dailyStreak + 1; streak <= dailyStreak + 30; streak++) {
            if (getDailyLoginRewardProp(streak) != NONE) {
                return streak;
            }
        }
        return NONE;
    }

    private int getDailyLoginRewardProp(int streak) {
        if (streak == 3) {
            return PROP_ROCKET;
        } else if (streak == 7) {
            return PROP_AURORA_ORB;
        } else if (streak == 14) {
            return PROP_STAR_COMPASS;
        } else if (streak == 21) {
            return PROP_BUBBLE_WAND;
        } else if (streak == 45) {
            return PROP_STAR_HARP;
        } else if (streak > 0 && streak % 30 == 0) {
            return PROP_SNOW_GLOBE;
        }
        return NONE;
    }

    private int getDailyLoginRewardAmount(int prop) {
        return prop == PROP_SNOW_GLOBE ? 2 : (prop == NONE ? 0 : 1);
    }

    private void loadDailyGoal() {
        long today = getToday();
        if (prefs.getLong(KEY_DAILY_GOAL_DAY, -1L) != today) {
            dailyGoalProgress = 0;
            dailyGoalClaimed = false;
            prefs.edit()
                    .putLong(KEY_DAILY_GOAL_DAY, today)
                    .putInt(KEY_DAILY_GOAL_PROGRESS, dailyGoalProgress)
                    .putBoolean(KEY_DAILY_GOAL_CLAIMED, false)
                    .apply();
            return;
        }

        dailyGoalProgress = prefs.getInt(KEY_DAILY_GOAL_PROGRESS, 0);
        dailyGoalClaimed = prefs.getBoolean(KEY_DAILY_GOAL_CLAIMED, false);
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

        if (dailyGoalRect.contains(x, y)) {
            claimDailyGoalReward();
            return;
        }

        if (chapterChestRect.contains(x, y)) {
            claimChapterChest();
            return;
        }

        if (replayHintRect.contains(x, y)) {
            startReplayTargetLevel();
            return;
        }

        if (starChestRect.contains(x, y)) {
            claimStarChest();
            return;
        }

        if (rankChestRect.contains(x, y)) {
            claimRankChest();
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
            lastRankChestReward = 0;
            lastChestRewardProp = NONE;
            lastChestRewardAmount = 0;
            lastChapterChestReward = 0;
            lastDailyGoalReward = 0;
            lastChestNoticeType = 1;
            chestNoticeUntilTime = System.currentTimeMillis() + 1400;
            return;
        }

        // 星级宝箱鼓励玩家反复挑战拿满星，并补充道具购买金币。
        starChestClaimed++;
        lastChestReward = 25 + starChestClaimed * 5;
        lastRankChestReward = 0;
        grantStarChestPropReward();
        lastChapterChestReward = 0;
        lastDailyGoalReward = 0;
        lastChestNoticeType = 1;
        coins += lastChestReward;
        prefs.edit()
                .putInt(KEY_STAR_CHEST_CLAIMED, starChestClaimed)
                .putInt(KEY_COINS, coins)
                .apply();
        chestNoticeUntilTime = System.currentTimeMillis() + 1800;
        playHaptic(HapticFeedbackConstants.CONFIRM);
        playSuccessTone();
    }

    private void claimRankChest() {
        int available = getAvailableRankChests();
        if (available <= 0) {
            lastChestReward = 0;
            lastRankChestReward = 0;
            lastChestRewardProp = NONE;
            lastChestRewardAmount = 0;
            lastChapterChestReward = 0;
            lastDailyGoalReward = 0;
            lastChestNoticeType = 2;
            chestNoticeUntilTime = System.currentTimeMillis() + 1400;
            return;
        }

        // 评级宝箱鼓励玩家重复挑战高分、连击和SSS评级。
        rankChestClaimed++;
        lastRankChestReward = 40 + rankChestClaimed * 8;
        lastChestReward = 0;
        grantRankChestPropReward();
        lastChapterChestReward = 0;
        lastDailyGoalReward = 0;
        lastChestNoticeType = 2;
        coins += lastRankChestReward;
        prefs.edit()
                .putInt(KEY_RANK_CHEST_CLAIMED, rankChestClaimed)
                .putInt(KEY_COINS, coins)
                .apply();
        chestNoticeUntilTime = System.currentTimeMillis() + 1800;
        playHaptic(HapticFeedbackConstants.CONFIRM);
        playSuccessTone();
    }

    private void grantStarChestPropReward() {
        lastChestRewardProp = NONE;
        lastChestRewardAmount = 0;
        lastChestRewardProp = getStarChestRewardProp(starChestClaimed);
        lastChestRewardAmount = getStarChestRewardAmount(lastChestRewardProp);
        if (lastChestRewardProp != NONE) {
            // 星级宝箱节点追加稀有道具，鼓励补星刷满。
            addReserveProp(lastChestRewardProp, lastChestRewardAmount);
        }
    }

    private int getStarChestRewardProp(int claimedCount) {
        if (claimedCount % 16 == 0) {
            return PROP_SNOW_GLOBE;
        } else if (claimedCount % 12 == 0) {
            return PROP_BUBBLE_WAND;
        } else if (claimedCount % 8 == 0) {
            return PROP_STAR_COMPASS;
        } else if (claimedCount % 4 == 0) {
            return PROP_AURORA_ORB;
        }
        return NONE;
    }

    private int getStarChestRewardAmount(int prop) {
        return prop == PROP_SNOW_GLOBE ? 2 : (prop == NONE ? 0 : 1);
    }

    private void grantRankChestPropReward() {
        lastChestRewardProp = NONE;
        lastChestRewardAmount = 0;
        lastChestRewardProp = getRankChestRewardProp(rankChestClaimed);
        lastChestRewardAmount = getRankChestRewardAmount(lastChestRewardProp);
        if (lastChestRewardProp != NONE) {
            // 评级宝箱节点奖励更偏向冲榜和高连击关卡。
            addReserveProp(lastChestRewardProp, lastChestRewardAmount);
        }
    }

    private int getRankChestRewardProp(int claimedCount) {
        if (claimedCount % 8 == 0) {
            return PROP_FIREWORK_CANNON;
        } else if (claimedCount % 4 == 0) {
            return PROP_MOON_TICKET;
        }
        return NONE;
    }

    private int getRankChestRewardAmount(int prop) {
        return prop == PROP_MOON_TICKET ? 2 : (prop == NONE ? 0 : 1);
    }

    private void claimChapterChest() {
        int chapter = getCurrentMapChapter();
        if (!canClaimChapterChest(chapter)) {
            lastChapterChestReward = 0;
            lastChestReward = 0;
            lastRankChestReward = 0;
            lastChestRewardProp = NONE;
            lastChestRewardAmount = 0;
            lastDailyGoalReward = 0;
            lastChestNoticeType = 3;
            chestNoticeUntilTime = System.currentTimeMillis() + 1400;
            return;
        }

        // 章节宝箱奖励玩家把整章刷到高星，形成中期追求。
        chapterChestClaimed[chapter] = true;
        lastChapterChestReward = 80 + chapter * 20;
        lastChestReward = 0;
        lastRankChestReward = 0;
        lastChestRewardProp = NONE;
        lastChestRewardAmount = 0;
        lastDailyGoalReward = 0;
        lastChestNoticeType = 3;
        coins += lastChapterChestReward;
        // 章节宝箱追加一个长期储备道具，让整章刷星奖励更有实用价值。
        lastChestRewardProp = getChapterChestRewardProp(chapter);
        lastChestRewardAmount = getChapterChestRewardAmount(chapter);
        addReserveProp(lastChestRewardProp, lastChestRewardAmount);
        prefs.edit()
                .putBoolean(KEY_CHAPTER_CHEST_PREFIX + chapter, true)
                .putInt(KEY_COINS, coins)
                .apply();
        chestNoticeUntilTime = System.currentTimeMillis() + 1800;
        playHaptic(HapticFeedbackConstants.CONFIRM);
        playSuccessTone();
    }

    private int getChapterChestRewardProp(int chapter) {
        return CHAPTER_CHEST_PROPS[Math.min(chapter, CHAPTER_CHEST_PROPS.length - 1)];
    }

    private int getChapterChestRewardAmount(int chapter) {
        return CHAPTER_CHEST_PROP_AMOUNTS[Math.min(chapter, CHAPTER_CHEST_PROP_AMOUNTS.length - 1)];
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

    private void placeFlower(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (flower[row][col] == 0 && shell[row][col] == 0 && ice[row][col] == 0
                    && honey[row][col] == 0 && stone[row][col] == 0 && vine[row][col] == 0
                    && gift[row][col] == 0 && chain[row][col] == 0) {
                // 花苞要先打裂再绽放，作为后期的双击清障目标。
                flower[row][col] = 2;
                placed++;
            }
        }
    }

    private void placeCoralReef(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (coralReef[row][col] == 0 && flower[row][col] == 0 && shell[row][col] == 0
                    && ice[row][col] == 0 && honey[row][col] == 0 && stone[row][col] == 0
                    && vine[row][col] == 0 && gift[row][col] == 0 && chain[row][col] == 0) {
                // 珊瑚礁需要两次打击，作为珊瑚集市后的主题障碍。
                coralReef[row][col] = 2;
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
            if (moveChest[row][col] == 0 && gift[row][col] == 0 && stone[row][col] == 0
                    && vine[row][col] == 0 && flower[row][col] == 0) {
                moveChest[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeCloud(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (cloud[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && flower[row][col] == 0) {
                cloud[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeGem(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (gem[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && flower[row][col] == 0) {
                // 钻石格是中期后的金币奖励点，清掉就有即时收益。
                gem[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeGoldenEgg(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (goldenEgg[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && flower[row][col] == 0) {
                // 黄金蛋是高价值金币格，鼓励玩家在复杂局面里规划额外收益。
                goldenEgg[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeCoinPouch(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (coinPouch[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && flower[row][col] == 0) {
                // 金币袋是后期经济奖励格，清掉后给更多金币补给。
                coinPouch[row][col] = 1;
                placed++;
            }
        }
    }

    private void placePaintBucket(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (paintBucket[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && coinPouch[row][col] == 0 && flower[row][col] == 0) {
                // 染色桶清除后会把周围棋子染成目标色，帮助玩家制造收集连锁。
                paintBucket[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeWindmill(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (windmill[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && flower[row][col] == 0) {
                // 风车被清除后会横扫一整行或一整列，制造额外连锁爽感。
                windmill[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeJewelBow(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (jewelBow[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && flower[row][col] == 0) {
                // 宝石蝴蝶结会把几枚棋子补成目标色，帮助后期收集目标续上节奏。
                jewelBow[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeStardustJar(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (stardustJar[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && flower[row][col] == 0) {
                // 星尘罐提供能量并生成方向特效，提升后期连锁爆发。
                stardustJar[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeWishLamp(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (wishLamp[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && flower[row][col] == 0) {
                // 许愿灯会直接推进目标收集，降低后期卡目标色的挫败感。
                wishLamp[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeResonanceDrum(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (resonanceDrum[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0
                    && flower[row][col] == 0) {
                // 共鸣鼓清除后立刻进入爆发节奏，鼓励玩家把奖励格串成连锁。
                resonanceDrum[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeAuroraPrism(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (auroraPrism[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0
                    && resonanceDrum[row][col] == 0 && flower[row][col] == 0) {
                // 极光棱镜会生成彩虹棋并补能量，是极光城堡后期的爆发奖励格。
                auroraPrism[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeRainbowBottle(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (rainbowBottle[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0
                    && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0 && flower[row][col] == 0) {
                // 彩虹瓶能生成彩虹棋，给后期关卡增加主动制造大招的机会。
                rainbowBottle[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeEnergyPotion(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (energyPotion[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0
                    && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0 && flower[row][col] == 0) {
                // 能量药水补充连击能量，让玩家更稳定地触发爆发奖励。
                energyPotion[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeButterfly(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (butterfly[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0 && rainbowBottle[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0
                    && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0 && energyPotion[row][col] == 0 && flower[row][col] == 0) {
                // 蝴蝶清除后会飞去帮忙处理一个关键目标，增加连锁后的惊喜感。
                butterfly[row][col] = 1;
                placed++;
            }
        }
    }

    private void placePortal(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (portal[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0
                    && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0
                    && butterfly[row][col] == 0 && flower[row][col] == 0) {
                // 传送门格被清掉后会扰动棋盘，制造新的连锁机会。
                portal[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeHourglass(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (hourglass[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0
                    && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0
                    && butterfly[row][col] == 0 && flower[row][col] == 0) {
                // 沙漏格提供额外步数，是后期关卡的翻盘奖励点。
                hourglass[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeLuckyStar(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (luckyStar[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0
                    && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0
                    && butterfly[row][col] == 0 && flower[row][col] == 0) {
                // 幸运星清除后直接补随机道具，增加关卡里的惊喜节奏。
                luckyStar[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeLuckyClover(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (luckyClover[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && coinPouch[row][col] == 0
                    && paintBucket[row][col] == 0 && windmill[row][col] == 0 && jewelBow[row][col] == 0
                    && stardustJar[row][col] == 0 && wishLamp[row][col] == 0 && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0
                    && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0
                    && butterfly[row][col] == 0 && flower[row][col] == 0) {
                // 幸运草随机送金币、步数、能量或道具，给尾声关卡增加翻盘惊喜。
                luckyClover[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeMysteryBox(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (mysteryBox[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && luckyClover[row][col] == 0 && coinPouch[row][col] == 0
                    && paintBucket[row][col] == 0 && windmill[row][col] == 0 && jewelBow[row][col] == 0
                    && stardustJar[row][col] == 0 && wishLamp[row][col] == 0 && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0
                    && energyPotion[row][col] == 0 && butterfly[row][col] == 0 && flower[row][col] == 0) {
                // 神秘盒清除后随机变成局内奖励，给后期关卡多一点不可预测收益。
                mysteryBox[row][col] = 1;
                placed++;
            }
        }
    }

    private void placePearl(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (pearl[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && luckyClover[row][col] == 0 && mysteryBox[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0 && jewelBow[row][col] == 0
                    && stardustJar[row][col] == 0 && wishLamp[row][col] == 0 && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0
                    && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0 && butterfly[row][col] == 0 && flower[row][col] == 0
                    && countdownBomb[row][col] == 0) {
                // 贝壳珍珠是珊瑚章节后的高价值奖励格，清掉后补金币和海星镐。
                pearl[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeCarousel(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (carousel[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && luckyClover[row][col] == 0 && mysteryBox[row][col] == 0
                    && pearl[row][col] == 0 && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0 && resonanceDrum[row][col] == 0
                    && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0
                    && butterfly[row][col] == 0 && flower[row][col] == 0 && countdownBomb[row][col] == 0) {
                // 旋转木马触发后旋转棋盘外圈，给月光游乐园增加更明显的机关感。
                carousel[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeFerrisTicket(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (ferrisTicket[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && luckyClover[row][col] == 0 && mysteryBox[row][col] == 0
                    && pearl[row][col] == 0 && carousel[row][col] == 0 && coinPouch[row][col] == 0
                    && paintBucket[row][col] == 0 && windmill[row][col] == 0 && jewelBow[row][col] == 0
                    && stardustJar[row][col] == 0 && wishLamp[row][col] == 0 && resonanceDrum[row][col] == 0
                    && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0
                    && butterfly[row][col] == 0 && flower[row][col] == 0 && countdownBomb[row][col] == 0) {
                // 摩天轮票根在终章提供月票和扫线奖励，强化游乐园主题收益。
                ferrisTicket[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeFireworksBarrel(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (fireworksBarrel[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && luckyClover[row][col] == 0 && mysteryBox[row][col] == 0
                    && pearl[row][col] == 0 && carousel[row][col] == 0 && ferrisTicket[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0
                    && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0
                    && energyPotion[row][col] == 0 && butterfly[row][col] == 0 && flower[row][col] == 0
                    && countdownBomb[row][col] == 0) {
                // 烟花桶清除后点燃爆炸特效，让终章关卡有更爽快的连锁爆点。
                fireworksBarrel[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeStarportBeacon(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (starportBeacon[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && luckyClover[row][col] == 0 && mysteryBox[row][col] == 0
                    && pearl[row][col] == 0 && carousel[row][col] == 0 && ferrisTicket[row][col] == 0
                    && fireworksBarrel[row][col] == 0 && coinPouch[row][col] == 0 && paintBucket[row][col] == 0
                    && windmill[row][col] == 0 && jewelBow[row][col] == 0 && stardustJar[row][col] == 0
                    && wishLamp[row][col] == 0 && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0
                    && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0 && butterfly[row][col] == 0
                    && flower[row][col] == 0 && countdownBomb[row][col] == 0) {
                // 星港信标会把棋盘点亮成特效节点，突出第十章的庆典连锁。
                starportBeacon[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeMeteorTrail(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (meteorTrail[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && luckyClover[row][col] == 0 && mysteryBox[row][col] == 0
                    && pearl[row][col] == 0 && carousel[row][col] == 0 && ferrisTicket[row][col] == 0
                    && fireworksBarrel[row][col] == 0 && starportBeacon[row][col] == 0 && coinPouch[row][col] == 0
                    && paintBucket[row][col] == 0 && windmill[row][col] == 0 && jewelBow[row][col] == 0
                    && stardustJar[row][col] == 0 && wishLamp[row][col] == 0 && resonanceDrum[row][col] == 0
                    && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0
                    && butterfly[row][col] == 0 && flower[row][col] == 0 && countdownBomb[row][col] == 0) {
                // 流星航线触发后划过双对角线，制造终章更强的斜向连锁。
                meteorTrail[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeRainbowArc(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (rainbowArc[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && luckyClover[row][col] == 0 && mysteryBox[row][col] == 0
                    && pearl[row][col] == 0 && carousel[row][col] == 0 && ferrisTicket[row][col] == 0
                    && fireworksBarrel[row][col] == 0 && starportBeacon[row][col] == 0 && meteorTrail[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0
                    && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0
                    && energyPotion[row][col] == 0 && butterfly[row][col] == 0 && flower[row][col] == 0
                    && countdownBomb[row][col] == 0) {
                // 彩虹拱桥会把清除收益转成彩虹棋，服务流星彩虹谷的高连锁节奏。
                rainbowArc[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeCrystalCore(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (crystalCore[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && luckyClover[row][col] == 0 && mysteryBox[row][col] == 0
                    && pearl[row][col] == 0 && carousel[row][col] == 0 && ferrisTicket[row][col] == 0
                    && fireworksBarrel[row][col] == 0 && starportBeacon[row][col] == 0 && meteorTrail[row][col] == 0
                    && rainbowArc[row][col] == 0 && coinPouch[row][col] == 0 && paintBucket[row][col] == 0
                    && windmill[row][col] == 0 && jewelBow[row][col] == 0 && stardustJar[row][col] == 0
                    && wishLamp[row][col] == 0 && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0
                    && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0 && butterfly[row][col] == 0
                    && flower[row][col] == 0 && countdownBomb[row][col] == 0) {
                // 糖晶塔芯会把终章清除转成爆炸棋，强化奇迹糖晶塔的连锁爆发。
                crystalCore[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeMusicBox(int count) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (musicBox[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && luckyClover[row][col] == 0 && mysteryBox[row][col] == 0
                    && pearl[row][col] == 0 && carousel[row][col] == 0 && ferrisTicket[row][col] == 0
                    && fireworksBarrel[row][col] == 0 && starportBeacon[row][col] == 0 && meteorTrail[row][col] == 0
                    && rainbowArc[row][col] == 0 && crystalCore[row][col] == 0 && coinPouch[row][col] == 0
                    && paintBucket[row][col] == 0 && windmill[row][col] == 0 && jewelBow[row][col] == 0
                    && stardustJar[row][col] == 0 && wishLamp[row][col] == 0 && resonanceDrum[row][col] == 0
                    && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0
                    && butterfly[row][col] == 0 && flower[row][col] == 0 && countdownBomb[row][col] == 0) {
                // 音乐盒清除后补星弦琴并制造特效，服务最终章的乐章连锁主题。
                musicBox[row][col] = 1;
                placed++;
            }
        }
    }

    private void placeCountdownBomb(int count, int timer) {
        int placed = 0;
        while (placed < count) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            if (countdownBomb[row][col] == 0 && gift[row][col] == 0 && moveChest[row][col] == 0
                    && cloud[row][col] == 0 && gem[row][col] == 0 && goldenEgg[row][col] == 0 && portal[row][col] == 0
                    && hourglass[row][col] == 0 && luckyStar[row][col] == 0 && luckyClover[row][col] == 0 && mysteryBox[row][col] == 0
                    && coinPouch[row][col] == 0 && paintBucket[row][col] == 0 && windmill[row][col] == 0
                    && jewelBow[row][col] == 0 && stardustJar[row][col] == 0 && wishLamp[row][col] == 0
                    && resonanceDrum[row][col] == 0 && auroraPrism[row][col] == 0 && rainbowBottle[row][col] == 0 && energyPotion[row][col] == 0
                    && butterfly[row][col] == 0 && flower[row][col] == 0 && pearl[row][col] == 0 && carousel[row][col] == 0
                    && ferrisTicket[row][col] == 0 && fireworksBarrel[row][col] == 0 && starportBeacon[row][col] == 0
                    && meteorTrail[row][col] == 0 && rainbowArc[row][col] == 0 && crystalCore[row][col] == 0
                    && musicBox[row][col] == 0) {
                // 倒计时炸弹必须在归零前清掉，给后期关卡制造明确压力。
                countdownBomb[row][col] = timer + random.nextInt(3);
                placed++;
            }
        }
    }

    private void removeCells(Set<Cell> cells) {
        for (Cell cell : cells) {
            int piece = board[cell.row][cell.col];
            boolean hadRewardCell = hasRewardCell(cell.row, cell.col);
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
            if (coralReef[cell.row][cell.col] > 0) {
                coralReef[cell.row][cell.col]--;
                if (coralReef[cell.row][cell.col] == 0) {
                    coralReefRemaining--;
                    score += 140;
                }
            }
            if (flower[cell.row][cell.col] > 0) {
                flower[cell.row][cell.col]--;
                if (flower[cell.row][cell.col] == 0) {
                    flowerRemaining--;
                    score += 160;
                    lastFlowerReward += 160;
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
            if (cloud[cell.row][cell.col] > 0) {
                // 彩云格提供额外分数，鼓励玩家优先规划高价值格。
                cloud[cell.row][cell.col] = 0;
                score += 120;
                lastCloudReward += 120;
            }
            if (gem[cell.row][cell.col] > 0) {
                gem[cell.row][cell.col] = 0;
                score += 180;
                coins += 3;
                lastGemReward += 3;
                saveCoins();
            }
            if (goldenEgg[cell.row][cell.col] > 0) {
                goldenEgg[cell.row][cell.col] = 0;
                score += 260;
                coins += 6;
                lastGoldenEggReward += 6;
                saveCoins();
            }
            if (coinPouch[cell.row][cell.col] > 0) {
                coinPouch[cell.row][cell.col] = 0;
                score += 220;
                coins += 8;
                lastCoinPouchReward += 8;
                saveCoins();
            }
            if (paintBucket[cell.row][cell.col] > 0) {
                paintBucket[cell.row][cell.col] = 0;
                int painted = paintAround(cell.row, cell.col);
                score += 120 + painted * 35;
                lastPaintBucketReward += Math.max(1, painted);
            }
            if (windmill[cell.row][cell.col] > 0) {
                windmill[cell.row][cell.col] = 0;
                triggerWindmill(cell.row, cell.col);
                score += 160;
                lastWindmillReward++;
            }
            if (jewelBow[cell.row][cell.col] > 0) {
                jewelBow[cell.row][cell.col] = 0;
                int changed = applyJewelBowTargetColor();
                score += 140 + changed * 45;
                lastJewelBowReward += Math.max(1, changed);
            }
            if (stardustJar[cell.row][cell.col] > 0) {
                stardustJar[cell.row][cell.col] = 0;
                comboEnergy = Math.min(100, comboEnergy + 25);
                upgradeRandomDirectionalPiece();
                score += 160;
                lastStardustJarReward += 25;
            }
            if (wishLamp[cell.row][cell.col] > 0) {
                wishLamp[cell.row][cell.col] = 0;
                int wished = applyWishLampTarget();
                score += 150 + wished * 55;
                lastWishLampReward += wished;
            }
            if (resonanceDrum[cell.row][cell.col] > 0) {
                resonanceDrum[cell.row][cell.col] = 0;
                comboEnergy = Math.min(100, comboEnergy + 40);
                comboFeverMoves = Math.max(comboFeverMoves, 2);
                score += 180;
                lastResonanceDrumReward += 2;
            }
            if (auroraPrism[cell.row][cell.col] > 0) {
                auroraPrism[cell.row][cell.col] = 0;
                comboEnergy = Math.min(100, comboEnergy + 30);
                upgradeRandomRainbowPiece();
                score += 200;
                lastAuroraPrismReward++;
            }
            if (rainbowBottle[cell.row][cell.col] > 0) {
                rainbowBottle[cell.row][cell.col] = 0;
                upgradeRandomRainbowPiece();
                lastRainbowBottleReward++;
                score += 180;
            }
            if (energyPotion[cell.row][cell.col] > 0) {
                energyPotion[cell.row][cell.col] = 0;
                comboEnergy = Math.min(100, comboEnergy + 35);
                lastEnergyPotionReward += 35;
                score += 120;
            }
            if (butterfly[cell.row][cell.col] > 0) {
                butterfly[cell.row][cell.col] = 0;
                triggerButterflyAssist();
                lastButterflyReward++;
                score += 120;
            }
            if (portal[cell.row][cell.col] > 0) {
                portal[cell.row][cell.col] = 0;
                triggerPortalShift();
            }
            if (hourglass[cell.row][cell.col] > 0) {
                hourglass[cell.row][cell.col] = 0;
                movesLeft += 3;
                moveLimitBonus += 3;
                score += 150;
                lastHourglassReward += 3;
            }
            if (luckyStar[cell.row][cell.col] > 0) {
                luckyStar[cell.row][cell.col] = 0;
                lastLuckyStarRewardProp = random.nextInt(PROP_COUNT);
                addProp(lastLuckyStarRewardProp, 1);
                score += 140;
            }
            if (luckyClover[cell.row][cell.col] > 0) {
                luckyClover[cell.row][cell.col] = 0;
                openLuckyClover();
                score += 150;
            }
            if (mysteryBox[cell.row][cell.col] > 0) {
                mysteryBox[cell.row][cell.col] = 0;
                openMysteryBox();
            }
            if (pearl[cell.row][cell.col] > 0) {
                pearl[cell.row][cell.col] = 0;
                score += 240;
                coins += 5;
                addProp(PROP_STARFISH_PICK, 1);
                lastPearlReward += 5;
                saveCoins();
            }
            if (carousel[cell.row][cell.col] > 0) {
                carousel[cell.row][cell.col] = 0;
                rotateBoardRing();
                comboEnergy = Math.min(100, comboEnergy + 20);
                score += 180;
                lastCarouselReward += 20;
            }
            if (ferrisTicket[cell.row][cell.col] > 0) {
                ferrisTicket[cell.row][cell.col] = 0;
                addProp(PROP_MOON_TICKET, 1);
                triggerFerrisTicketSweep(cell.row, cell.col);
                score += 200;
                lastFerrisTicketReward++;
            }
            if (fireworksBarrel[cell.row][cell.col] > 0) {
                fireworksBarrel[cell.row][cell.col] = 0;
                triggerFireworksBarrel(cell.row, cell.col);
                comboEnergy = Math.min(100, comboEnergy + 25);
                score += 220;
                lastFireworksBarrelReward += 25;
            }
            if (starportBeacon[cell.row][cell.col] > 0) {
                starportBeacon[cell.row][cell.col] = 0;
                triggerStarportBeacon();
                comboEnergy = Math.min(100, comboEnergy + 35);
                score += 240;
                lastStarportBeaconReward += 35;
            }
            if (meteorTrail[cell.row][cell.col] > 0) {
                meteorTrail[cell.row][cell.col] = 0;
                triggerMeteorTrail(cell.row, cell.col);
                coins += 4;
                score += 220;
                lastMeteorTrailReward += 4;
                saveCoins();
            }
            if (rainbowArc[cell.row][cell.col] > 0) {
                rainbowArc[cell.row][cell.col] = 0;
                upgradeRandomRainbowPiece();
                comboEnergy = Math.min(100, comboEnergy + 28);
                score += 260;
                lastRainbowArcReward += 28;
            }
            if (crystalCore[cell.row][cell.col] > 0) {
                crystalCore[cell.row][cell.col] = 0;
                upgradeRandomBombPiece();
                upgradeRandomBombPiece();
                comboEnergy = Math.min(100, comboEnergy + 18);
                score += 320;
                lastCrystalCoreReward += 2;
            }
            if (musicBox[cell.row][cell.col] > 0) {
                musicBox[cell.row][cell.col] = 0;
                // 音乐盒作为资源型回访目标，星弦琴同时进入储备并带到后续关卡。
                addReserveProp(PROP_STAR_HARP, 1);
                upgradeRandomDirectionalPiece();
                comboEnergy = Math.min(100, comboEnergy + 32);
                score += 300;
                lastMusicBoxReward++;
            }
            if (countdownBomb[cell.row][cell.col] > 0) {
                countdownBomb[cell.row][cell.col] = 0;
                score += 180;
                lastCountdownBombReward++;
            }
            if (hadRewardCell) {
                rewardCellClearedCount++;
            }
            board[cell.row][cell.col] = NONE;
        }
    }

    private int paintAround(int row, int col) {
        int painted = 0;
        for (Cell cell : buildBombCells(row, col)) {
            if (board[cell.row][cell.col] != NONE && specialOf(board[cell.row][cell.col]) == SPECIAL_NORMAL) {
                board[cell.row][cell.col] = makePiece(targetKind, SPECIAL_NORMAL);
                painted++;
            }
        }
        return painted;
    }

    private void triggerWindmill(int row, int col) {
        Set<Cell> cells = new HashSet<>();
        if (random.nextBoolean()) {
            for (int sweepCol = 0; sweepCol < BOARD_SIZE; sweepCol++) {
                cells.add(new Cell(row, sweepCol));
            }
        } else {
            for (int sweepRow = 0; sweepRow < BOARD_SIZE; sweepRow++) {
                cells.add(new Cell(sweepRow, col));
            }
        }
        cells.remove(new Cell(row, col));
        removeCells(cells);
    }

    private void triggerFerrisTicketSweep(int row, int col) {
        Set<Cell> cells = random.nextBoolean() ? buildCrossCells(row, col) : buildRocketCells(row, col);
        cells.remove(new Cell(row, col));
        removeCells(cells);
    }

    private void triggerFireworksBarrel(int row, int col) {
        Set<Cell> cells = buildBombCells(row, col);
        cells.remove(new Cell(row, col));
        removeCells(cells);
        upgradeRandomBombPiece();
    }

    private void triggerStarportBeacon() {
        for (int i = 0; i < 3; i++) {
            upgradeRandomDirectionalPiece();
        }
    }

    private void triggerMeteorTrail(int row, int col) {
        Set<Cell> cells = buildDiagonalCells(row, col);
        cells.remove(new Cell(row, col));
        removeCells(cells);
    }

    private void rotateBoardRing() {
        List<Integer> ring = new ArrayList<>();
        for (int col = 0; col < BOARD_SIZE; col++) {
            ring.add(board[0][col]);
        }
        for (int row = 1; row < BOARD_SIZE; row++) {
            ring.add(board[row][BOARD_SIZE - 1]);
        }
        for (int col = BOARD_SIZE - 2; col >= 0; col--) {
            ring.add(board[BOARD_SIZE - 1][col]);
        }
        for (int row = BOARD_SIZE - 2; row > 0; row--) {
            ring.add(board[row][0]);
        }
        if (ring.isEmpty()) {
            return;
        }

        int shifted = ring.remove(ring.size() - 1);
        ring.add(0, shifted);
        int index = 0;
        for (int col = 0; col < BOARD_SIZE; col++) {
            board[0][col] = ring.get(index++);
        }
        for (int row = 1; row < BOARD_SIZE; row++) {
            board[row][BOARD_SIZE - 1] = ring.get(index++);
        }
        for (int col = BOARD_SIZE - 2; col >= 0; col--) {
            board[BOARD_SIZE - 1][col] = ring.get(index++);
        }
        for (int row = BOARD_SIZE - 2; row > 0; row--) {
            board[row][0] = ring.get(index++);
        }
    }

    private int applyJewelBowTargetColor() {
        List<Cell> candidates = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != NONE && specialOf(board[row][col]) == SPECIAL_NORMAL
                        && colorOf(board[row][col]) != targetKind) {
                    candidates.add(new Cell(row, col));
                }
            }
        }

        int changed = 0;
        int changeCount = Math.min(4, candidates.size());
        while (changed < changeCount) {
            Cell cell = candidates.remove(random.nextInt(candidates.size()));
            board[cell.row][cell.col] = makePiece(targetKind, SPECIAL_NORMAL);
            changed++;
        }
        return changed;
    }

    private void triggerButterflyAssist() {
        Cell target = findButterflyAssistCell();
        if (target == null) {
            return;
        }

        Set<Cell> cells = new HashSet<>();
        cells.add(target);
        removeCells(cells);
    }

    private Cell findButterflyAssistCell() {
        List<Cell> candidates = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (ice[row][col] > 0 || honey[row][col] > 0 || stone[row][col] > 0 || vine[row][col] > 0
                        || chain[row][col] > 0 || shell[row][col] > 0 || coralReef[row][col] > 0 || flower[row][col] > 0) {
                    candidates.add(new Cell(row, col));
                }
            }
        }
        if (candidates.isEmpty()) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    if (board[row][col] != NONE && colorOf(board[row][col]) == targetKind) {
                        candidates.add(new Cell(row, col));
                    }
                }
            }
        }
        return candidates.isEmpty() ? null : candidates.get(random.nextInt(candidates.size()));
    }

    private void upgradeRandomDirectionalPiece() {
        List<Cell> candidates = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != NONE && specialOf(board[row][col]) == SPECIAL_NORMAL) {
                    candidates.add(new Cell(row, col));
                }
            }
        }
        if (candidates.isEmpty()) {
            return;
        }

        Cell cell = candidates.get(random.nextInt(candidates.size()));
        int special = random.nextBoolean() ? SPECIAL_ROW : SPECIAL_COLUMN;
        board[cell.row][cell.col] = makePiece(colorOf(board[cell.row][cell.col]), special);
    }

    private void upgradeRandomRainbowPiece() {
        List<Cell> candidates = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != NONE && specialOf(board[row][col]) == SPECIAL_NORMAL) {
                    candidates.add(new Cell(row, col));
                }
            }
        }
        if (candidates.isEmpty()) {
            return;
        }

        Cell cell = candidates.get(random.nextInt(candidates.size()));
        board[cell.row][cell.col] = makePiece(colorOf(board[cell.row][cell.col]), SPECIAL_RAINBOW);
    }

    private void upgradeRandomBombPiece() {
        List<Cell> candidates = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != NONE && specialOf(board[row][col]) == SPECIAL_NORMAL) {
                    candidates.add(new Cell(row, col));
                }
            }
        }
        if (candidates.isEmpty()) {
            return;
        }

        Cell cell = candidates.get(random.nextInt(candidates.size()));
        board[cell.row][cell.col] = makePiece(colorOf(board[cell.row][cell.col]), SPECIAL_BOMB);
    }

    private int applyWishLampTarget() {
        int wished = Math.min(5, targetRemaining);
        targetRemaining -= wished;
        return wished;
    }

    private void tickCountdownBombs() {
        Level level = levels.get(levelIndex);
        if (level.countdownBombCount <= 0 || levelComplete || levelFailed) {
            return;
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (countdownBomb[row][col] <= 0) {
                    continue;
                }
                countdownBomb[row][col]--;
                if (countdownBomb[row][col] <= 0) {
                    if (bombShieldCount > 0) {
                        bombShieldCount--;
                        countdownBomb[row][col] = 3;
                        lastShieldReward = bombShieldCount + 1;
                        lastTaskRewardType = 10;
                        showFeedback(1, lastShieldReward);
                        return;
                    }
                    countdownBombExploded = true;
                    levelFailed = true;
                    recordLevelFailure();
                    resetWinStreak();
                    return;
                }
            }
        }
    }

    private void extendCountdownBombs(int amount) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (countdownBomb[row][col] > 0) {
                    countdownBomb[row][col] += amount;
                }
            }
        }
    }

    private void triggerPortalShift() {
        Cell first = new Cell(random.nextInt(BOARD_SIZE), random.nextInt(BOARD_SIZE));
        Cell second = new Cell(random.nextInt(BOARD_SIZE), random.nextInt(BOARD_SIZE));
        if (first.equals(second)) {
            return;
        }

        swap(first.row, first.col, second.row, second.col);
        lastPortalReward++;
        score += 80;
    }

    private void openGift() {
        // 礼盒给随机惊喜，鼓励玩家顺手清理奖励格。
        if (random.nextBoolean()) {
            int reward = 5 + random.nextInt(8);
            coins += reward;
            lastGiftReward += reward;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            saveCoins();
        } else {
            addProp(random.nextInt(PROP_COUNT), 1);
            lastGiftReward++;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
        }
    }

    private void openLuckyClover() {
        int reward = random.nextInt(4);
        lastLuckyCloverRewardType = reward + 1;
        lastLuckyCloverRewardProp = NONE;
        lastLuckyStarRewardProp = NONE;
        lastMysteryRewardType = 0;
        lastMysteryRewardAmount = 0;
        lastMysteryRewardProp = NONE;
        lastPearlReward = 0;
        lastFerrisTicketReward = 0;
        lastFireworksBarrelReward = 0;
        lastStarportBeaconReward = 0;
        lastMeteorTrailReward = 0;
        lastRainbowArcReward = 0;
        lastCrystalCoreReward = 0;
        lastMusicBoxReward = 0;
        lastEnergyRewardProp = NONE;
        if (reward == 0) {
            lastLuckyCloverRewardAmount = 10;
            coins += lastLuckyCloverRewardAmount;
            saveCoins();
        } else if (reward == 1) {
            lastLuckyCloverRewardAmount = 2;
            movesLeft += lastLuckyCloverRewardAmount;
            moveLimitBonus += lastLuckyCloverRewardAmount;
        } else if (reward == 2) {
            lastLuckyCloverRewardAmount = 30;
            comboEnergy = Math.min(100, comboEnergy + lastLuckyCloverRewardAmount);
        } else {
            lastLuckyCloverRewardProp = random.nextInt(PROP_COUNT);
            lastLuckyCloverRewardAmount = 1;
            addProp(lastLuckyCloverRewardProp, 1);
        }
    }

    private void openMysteryBox() {
        int reward = random.nextInt(4);
        lastMysteryRewardType = reward + 1;
        lastMysteryRewardProp = NONE;
        lastLuckyCloverRewardType = 0;
        lastLuckyCloverRewardAmount = 0;
        lastLuckyCloverRewardProp = NONE;
        honeySpreadCount = 0;
        lastGiftReward = 0;
        lastMoveChestReward = 0;
        lastCloudReward = 0;
        lastFlowerReward = 0;
        lastGoldenEggReward = 0;
        lastCoinPouchReward = 0;
        lastPaintBucketReward = 0;
        lastWindmillReward = 0;
        lastJewelBowReward = 0;
        lastStardustJarReward = 0;
        lastWishLampReward = 0;
        lastResonanceDrumReward = 0;
        lastAuroraPrismReward = 0;
        lastRainbowBottleReward = 0;
        lastEnergyPotionReward = 0;
        lastButterflyReward = 0;
        lastGemReward = 0;
        lastPortalReward = 0;
        lastHourglassReward = 0;
        lastLuckyStarRewardProp = NONE;
        lastPearlReward = 0;
        lastFerrisTicketReward = 0;
        lastFireworksBarrelReward = 0;
        lastStarportBeaconReward = 0;
        lastMeteorTrailReward = 0;
        lastRainbowArcReward = 0;
        lastCrystalCoreReward = 0;
        lastMusicBoxReward = 0;
        lastEnergyRewardProp = NONE;
        if (reward == 0) {
            lastMysteryRewardAmount = 220;
            score += lastMysteryRewardAmount;
        } else if (reward == 1) {
            lastMysteryRewardAmount = 5;
            coins += lastMysteryRewardAmount;
            saveCoins();
        } else if (reward == 2) {
            lastMysteryRewardAmount = 2;
            movesLeft += lastMysteryRewardAmount;
            moveLimitBonus += lastMysteryRewardAmount;
        } else {
            lastMysteryRewardProp = random.nextInt(PROP_COUNT);
            lastMysteryRewardAmount = 1;
            addProp(lastMysteryRewardProp, 1);
        }
    }

    private void spreadHoneyAfterMove() {
        Level level = levels.get(levelIndex);
        if (honeyFreezeMoves > 0) {
            honeyFreezeMoves--;
            return;
        }
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
        lastCloudReward = 0;
        lastFlowerReward = 0;
        lastGoldenEggReward = 0;
        lastCoinPouchReward = 0;
        lastPaintBucketReward = 0;
        lastWindmillReward = 0;
        lastJewelBowReward = 0;
        lastStardustJarReward = 0;
        lastRainbowBottleReward = 0;
        lastEnergyPotionReward = 0;
        lastButterflyReward = 0;
        lastMysteryRewardType = 0;
        lastMysteryRewardAmount = 0;
        lastMysteryRewardProp = NONE;
        lastPearlReward = 0;
        lastFerrisTicketReward = 0;
        lastFireworksBarrelReward = 0;
        lastStarportBeaconReward = 0;
        lastMeteorTrailReward = 0;
        lastRainbowArcReward = 0;
        lastCrystalCoreReward = 0;
        lastMusicBoxReward = 0;
        lastEnergyRewardProp = NONE;
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
            addProp(PROP_HAMMER, targetMilestone - rewardTargetMilestone);
            rewardTargetMilestone = targetMilestone;
            lastTaskRewardType = 1;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, 5);
        }

        int clearedObstacles = level.iceCount + level.honeyCount + level.stoneCount + level.vineCount + level.chainCount
                + level.shellCount + level.coralReefCount + level.flowerCount - iceRemaining - honeyRemaining - stoneRemaining - vineRemaining
                - chainRemaining - shellRemaining - coralReefRemaining - flowerRemaining;
        int obstacleMilestone = clearedObstacles / 6;
        if (obstacleMilestone > rewardObstacleMilestone) {
            // 清障越积极，道具补给越快。
            addProp(PROP_BOMB, obstacleMilestone - rewardObstacleMilestone);
            rewardObstacleMilestone = obstacleMilestone;
            lastTaskRewardType = 2;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, 6);
        }

        int comboMilestone = bestCombo / 3;
        if (comboMilestone > rewardComboMilestone) {
            // 做出大连击时给随机补给，奖励更有技巧性的消除。
            addProp(random.nextInt(PROP_COUNT), comboMilestone - rewardComboMilestone);
            rewardComboMilestone = comboMilestone;
            lastTaskRewardType = 3;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastGoldenEggReward = 0;
            lastCoinPouchReward = 0;
            lastPaintBucketReward = 0;
            lastWindmillReward = 0;
            lastJewelBowReward = 0;
            lastStardustJarReward = 0;
            lastWishLampReward = 0;
            lastResonanceDrumReward = 0;
            lastAuroraPrismReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(Math.max(2, bestCombo), 3);
        }

        if (level.keyCount > 0 && keyRemaining <= 0 && rewardKeyMilestone == 0) {
            // 收齐钥匙后补一个强力道具，让额外目标有即时爽感。
            addProp(PROP_COLOR_BLAST, 1);
            rewardKeyMilestone = 1;
            lastTaskRewardType = 4;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, level.keyCount);
        }

        if (level.countdownBombCount > 0 && getCountdownBombRemainingCount() <= 0 && rewardBombMilestone == 0) {
            // 全部拆弹后返还护盾，让高压关卡的关键处理有一次明确补给。
            addProp(PROP_SHIELD, 1);
            rewardBombMilestone = 1;
            lastTaskRewardType = 24;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastCountdownBombReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, level.countdownBombCount);
        }

        if (level.musicBoxCount > 0 && getMusicBoxRemainingCount() <= 0 && rewardMusicBoxMilestone == 0) {
            // 全开音乐盒后额外储备一枚星弦琴，强化资源关的完整收集目标。
            addReserveProp(PROP_STAR_HARP, 1);
            rewardMusicBoxMilestone = 1;
            lastMusicBoxMilestoneReward = 1;
            lastTaskRewardType = 25;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastCountdownBombReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, level.musicBoxCount);
        }

        int rewardCellMilestoneNow = rewardCellClearedCount / 3;
        if (rewardCellMilestoneNow > rewardCellMilestone) {
            // 奖励格形成独立的局内累计目标，鼓励玩家主动规划高收益格。
            addProp(PROP_STAR_COMPASS, rewardCellMilestoneNow - rewardCellMilestone);
            lastRewardCellMilestoneAmount = rewardCellMilestoneNow - rewardCellMilestone;
            rewardCellMilestone = rewardCellMilestoneNow;
            lastTaskRewardType = 23;
            lastGiftReward = 0;
            honeySpreadCount = 0;
            lastMoveChestReward = 0;
            lastCloudReward = 0;
            lastFlowerReward = 0;
            lastRainbowBottleReward = 0;
            lastEnergyPotionReward = 0;
            lastButterflyReward = 0;
            lastMysteryRewardType = 0;
            lastMysteryRewardAmount = 0;
            lastMysteryRewardProp = NONE;
            lastPearlReward = 0;
            lastFerrisTicketReward = 0;
            lastFireworksBarrelReward = 0;
            lastStarportBeaconReward = 0;
            lastMeteorTrailReward = 0;
            lastRainbowArcReward = 0;
            lastCrystalCoreReward = 0;
            lastMusicBoxReward = 0;
            lastEnergyRewardProp = NONE;
            showFeedback(1, rewardCellClearedCount);
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
        drawChapterDecorations(canvas, chapter);
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

    private void drawChapterDecorations(Canvas canvas, int chapter) {
        long time = System.currentTimeMillis();
        paint.setColor(Color.argb(55, 255, 255, 255));
        if (chapter == 0) {
            for (int i = 0; i < 6; i++) {
                float x = (i * getWidth() / 5f) + (float) Math.sin(time / 900.0 + i) * dp(8);
                float y = getHeight() * (0.14f + (i % 3) * 0.18f);
                drawLeaf(canvas, x, y, dp(18 + i % 2 * 5));
            }
        } else if (chapter == 1) {
            for (int i = 0; i < 5; i++) {
                float x = getWidth() * (0.12f + i * 0.18f);
                float y = getHeight() * (0.18f + (i % 2) * 0.28f) + (float) Math.sin(time / 700.0 + i) * dp(5);
                canvas.drawCircle(x, y, dp(14), paint);
                canvas.drawCircle(x + dp(14), y + dp(3), dp(12), paint);
                canvas.drawCircle(x - dp(12), y + dp(5), dp(10), paint);
            }
        } else if (chapter == 2) {
            for (int i = 0; i < 5; i++) {
                Path shard = new Path();
                float x = getWidth() * (0.1f + i * 0.2f);
                float y = getHeight() * (0.2f + (i % 3) * 0.16f);
                shard.moveTo(x, y - dp(18));
                shard.lineTo(x + dp(16), y + dp(16));
                shard.lineTo(x - dp(14), y + dp(12));
                shard.close();
                canvas.drawPath(shard, paint);
            }
        } else if (chapter == 3) {
            for (int i = 0; i < 7; i++) {
                float x = getWidth() * (0.08f + i * 0.14f);
                float y = getHeight() * 0.22f + (i % 3) * dp(42);
                drawLeaf(canvas, x, y, dp(14), -35);
            }
        } else if (chapter == 4) {
            for (int i = 0; i < 9; i++) {
                float x = getWidth() * (0.08f + i * 0.11f);
                float y = getHeight() * (0.16f + (i % 4) * 0.13f);
                drawStar(canvas, x, y, dp(8 + i % 3));
            }
        } else if (chapter == 5) {
            for (int i = 0; i < 6; i++) {
                float x = getWidth() * (0.1f + i * 0.16f);
                float y = getHeight() * (0.18f + (i % 2) * 0.22f);
                canvas.drawCircle(x, y, dp(12), paint);
                canvas.drawCircle(x + dp(10), y + dp(10), dp(12), paint);
                canvas.drawCircle(x - dp(10), y + dp(10), dp(12), paint);
            }
        } else if (chapter == 6) {
            for (int i = 0; i < 7; i++) {
                float x = getWidth() * (0.08f + i * 0.14f);
                float y = getHeight() * (0.2f + (i % 3) * 0.14f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(3));
                canvas.drawArc(new RectF(x - dp(16), y - dp(12), x + dp(16), y + dp(12)),
                        200, 140, false, paint);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(x - dp(13), y + dp(5), dp(4), paint);
                canvas.drawCircle(x + dp(13), y + dp(5), dp(4), paint);
            }
        } else if (chapter == 7) {
            for (int i = 0; i < 8; i++) {
                float x = getWidth() * (0.08f + i * 0.12f);
                float y = getHeight() * (0.15f + (i % 4) * 0.12f);
                drawStar(canvas, x, y, dp(6 + i % 2));
                paint.setColor(Color.argb(45, 255, 255, 255));
                canvas.drawLine(x - dp(18), y + dp(12), x + dp(18), y - dp(12), paint);
                paint.setColor(Color.argb(55, 255, 255, 255));
            }
        } else if (chapter == 8) {
            for (int i = 0; i < 5; i++) {
                float x = getWidth() * (0.12f + i * 0.18f);
                float y = getHeight() * (0.17f + (i % 2) * 0.2f);
                // 月光游乐园用旋转轮廓和小星光营造更活泼的终章氛围。
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(2));
                canvas.drawCircle(x, y, dp(18), paint);
                for (int spoke = 0; spoke < 6; spoke++) {
                    double angle = time / 1000.0 + spoke * Math.PI / 3;
                    canvas.drawLine(x, y, x + (float) Math.cos(angle) * dp(18),
                            y + (float) Math.sin(angle) * dp(18), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                drawStar(canvas, x + dp(22), y - dp(18), dp(5 + i % 2));
            }
        } else if (chapter == 9) {
            for (int i = 0; i < 6; i++) {
                float x = getWidth() * (0.1f + i * 0.16f);
                float y = getHeight() * (0.16f + (i % 3) * 0.14f);
                // 烟花星港用升空轨迹和爆点表现终章后的庆典感。
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(2));
                canvas.drawLine(x, y + dp(24), x + (float) Math.sin(time / 800.0 + i) * dp(10), y, paint);
                paint.setStyle(Paint.Style.FILL);
                drawStar(canvas, x, y - dp(6), dp(7 + i % 3));
                canvas.drawCircle(x + dp(18), y + dp(8), dp(3 + i % 2), paint);
            }
        } else if (chapter == 10) {
            for (int i = 0; i < 8; i++) {
                float x = getWidth() * (0.08f + i * 0.12f);
                float y = getHeight() * (0.14f + (i % 4) * 0.13f);
                // 流星彩虹谷用弧形轨迹和星点强化新章节的流动感。
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(3));
                canvas.drawArc(new RectF(x - dp(22), y - dp(16), x + dp(22), y + dp(16)),
                        205, 120, false, paint);
                paint.setStyle(Paint.Style.FILL);
                drawStar(canvas, x + dp(18), y - dp(10), dp(6 + i % 3));
                canvas.drawCircle(x - dp(18), y + dp(12), dp(3 + i % 2), paint);
            }
        } else if (chapter == 11) {
            for (int i = 0; i < 7; i++) {
                float x = getWidth() * (0.1f + i * 0.14f);
                float y = getHeight() * (0.15f + (i % 3) * 0.15f);
                // 奇迹糖晶塔用晶体轮廓区分最终扩展章节。
                Path crystal = new Path();
                crystal.moveTo(x, y - dp(20));
                crystal.lineTo(x + dp(14), y - dp(2));
                crystal.lineTo(x + dp(8), y + dp(20));
                crystal.lineTo(x - dp(10), y + dp(18));
                crystal.lineTo(x - dp(15), y - dp(1));
                crystal.close();
                canvas.drawPath(crystal, paint);
                drawStar(canvas, x + dp(18), y - dp(16), dp(5 + i % 2));
            }
        } else if (chapter == 12) {
            for (int i = 0; i < 8; i++) {
                float x = getWidth() * (0.08f + i * 0.12f);
                float y = getHeight() * (0.15f + (i % 4) * 0.13f);
                // 泡泡星河岛用泡泡和星线做轻盈的后期主题。
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(2));
                canvas.drawCircle(x, y, dp(12 + i % 3 * 3), paint);
                canvas.drawLine(x - dp(16), y + dp(18), x + dp(16), y - dp(18), paint);
                paint.setStyle(Paint.Style.FILL);
                drawStar(canvas, x + dp(18), y - dp(16), dp(5 + i % 2));
            }
        } else if (chapter == 13) {
            for (int i = 0; i < 8; i++) {
                float x = getWidth() * (0.08f + i * 0.12f);
                float y = getHeight() * (0.16f + (i % 4) * 0.12f);
                // 花火薄荷城混合烟花和叶片，作为新终章的庆典视觉。
                drawLeaf(canvas, x - dp(10), y + dp(10), dp(12), 35);
                drawStar(canvas, x + dp(16), y - dp(12), dp(6 + i % 3));
                canvas.drawCircle(x, y, dp(4 + i % 2), paint);
            }
        } else if (chapter == 14) {
            for (int i = 0; i < 8; i++) {
                float x = getWidth() * (0.08f + i * 0.12f);
                float y = getHeight() * (0.15f + (i % 4) * 0.13f);
                // 星霜糖果港用冰晶星线表现更冷亮的后期港口。
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(2));
                canvas.drawLine(x - dp(18), y, x + dp(18), y, paint);
                canvas.drawLine(x, y - dp(18), x, y + dp(18), paint);
                canvas.drawLine(x - dp(12), y - dp(12), x + dp(12), y + dp(12), paint);
                canvas.drawLine(x + dp(12), y - dp(12), x - dp(12), y + dp(12), paint);
                paint.setStyle(Paint.Style.FILL);
                drawStar(canvas, x + dp(20), y - dp(14), dp(5 + i % 2));
            }
        } else if (chapter == 15) {
            for (int i = 0; i < 8; i++) {
                float x = getWidth() * (0.08f + i * 0.12f);
                float y = getHeight() * (0.16f + (i % 4) * 0.12f);
                // 彩云琉璃境用弧线和彩色星点收束 320 关长线目标。
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(3));
                canvas.drawArc(new RectF(x - dp(20), y - dp(10), x + dp(20), y + dp(22)),
                        205, 130, false, paint);
                paint.setStyle(Paint.Style.FILL);
                drawStar(canvas, x + dp(16), y - dp(12), dp(6 + i % 3));
                canvas.drawCircle(x - dp(16), y + dp(12), dp(3 + i % 2), paint);
            }
        } else if (chapter == 16) {
            for (int i = 0; i < 8; i++) {
                float x = getWidth() * (0.08f + i * 0.12f);
                float y = getHeight() * (0.15f + (i % 4) * 0.13f);
                // 蜜星钟楼用钟摆和星点表现后期计时压力。
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(2));
                canvas.drawCircle(x, y, dp(15), paint);
                canvas.drawLine(x, y, x + (float) Math.sin(time / 700.0 + i) * dp(10), y + dp(12), paint);
                canvas.drawLine(x, y, x, y - dp(9), paint);
                paint.setStyle(Paint.Style.FILL);
                drawStar(canvas, x + dp(18), y - dp(16), dp(5 + i % 2));
            }
        } else if (chapter == 17) {
            for (int i = 0; i < 9; i++) {
                float x = getWidth() * (0.07f + i * 0.11f);
                float y = getHeight() * (0.15f + (i % 4) * 0.13f);
                // 极彩乐章谷用音符般的弧线和棱镜星点做最终段视觉。
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(3));
                canvas.drawArc(new RectF(x - dp(18), y - dp(18), x + dp(18), y + dp(18)),
                        30, 250, false, paint);
                canvas.drawLine(x + dp(13), y + dp(8), x + dp(13), y + dp(24), paint);
                paint.setStyle(Paint.Style.FILL);
                drawStar(canvas, x - dp(15), y - dp(10), dp(6 + i % 3));
                canvas.drawCircle(x + dp(13), y + dp(25), dp(4), paint);
            }
        } else if (chapter == 18) {
            for (int i = 0; i < 9; i++) {
                float x = getWidth() * (0.07f + i * 0.11f);
                float y = getHeight() * (0.15f + (i % 4) * 0.13f);
                // 星弦幻音台用竖琴弧线强调星弦琴和音乐盒的连续爆发。
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(3));
                canvas.drawArc(new RectF(x - dp(20), y - dp(20), x + dp(20), y + dp(24)),
                        245, 230, false, paint);
                canvas.drawLine(x - dp(9), y - dp(12), x - dp(9), y + dp(18), paint);
                canvas.drawLine(x, y - dp(9), x, y + dp(15), paint);
                canvas.drawLine(x + dp(9), y - dp(5), x + dp(9), y + dp(12), paint);
                paint.setStyle(Paint.Style.FILL);
                drawStar(canvas, x + dp(20), y - dp(18), dp(5 + i % 2));
            }
        } else {
            for (int i = 0; i < 9; i++) {
                float x = getWidth() * (0.08f + i * 0.10f);
                float y = getHeight() * (0.15f + (i % 4) * 0.13f);
                // 糖彩乐园剧场用舞台灯和糖果星点收束 400 关长线目标。
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(2));
                canvas.drawLine(x - dp(16), y - dp(18), x, y + dp(18), paint);
                canvas.drawLine(x + dp(16), y - dp(18), x, y + dp(18), paint);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRoundRect(new RectF(x - dp(14), y + dp(14), x + dp(14), y + dp(24)),
                        dp(5), dp(5), paint);
                drawStar(canvas, x, y - dp(12), dp(6 + i % 3));
            }
        }
    }

    private void drawLeaf(Canvas canvas, float centerX, float centerY, float size) {
        drawLeaf(canvas, centerX, centerY, size, 28);
    }

    private void drawLeaf(Canvas canvas, float centerX, float centerY, float size, float tiltDegrees) {
        canvas.save();
        canvas.rotate(tiltDegrees, centerX, centerY);
        canvas.drawOval(new RectF(centerX - size * 0.45f, centerY - size * 0.2f,
                centerX + size * 0.45f, centerY + size * 0.2f), paint);
        canvas.restore();
    }

    private void drawStar(Canvas canvas, float centerX, float centerY, float radius) {
        Path star = new Path();
        for (int i = 0; i < 10; i++) {
            double angle = -Math.PI / 2 + i * Math.PI / 5;
            float r = i % 2 == 0 ? radius : radius * 0.45f;
            float x = centerX + (float) Math.cos(angle) * r;
            float y = centerY + (float) Math.sin(angle) * r;
            if (i == 0) {
                star.moveTo(x, y);
            } else {
                star.lineTo(x, y);
            }
        }
        star.close();
        canvas.drawPath(star, paint);
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
            if (lastDailyRewardProp != NONE) {
                coinText += " " + getPropName(lastDailyRewardProp) + "+" + lastDailyRewardPropAmount;
            }
        }
        if (dailyStreak > 1) {
            coinText += " 连" + dailyStreak;
        }
        if (!dailyChallengeMode && winStreak > 1) {
            coinText += " 胜" + winStreak + buildNextWinStreakRewardHint();
        }
        coinText += buildNextDailyLoginRewardHint();
        drawTextFitRight(canvas, coinText, new RectF(getWidth() * 0.48f, dp(92), getWidth() - dp(18), dp(110)), 15, Color.WHITE);
        String obstacleText = "冰" + iceRemaining + " 蜜" + honeyRemaining + " 石" + stoneRemaining
                + " 藤" + vineRemaining + " 锁" + chainRemaining;
        if (level.shellCount > 0) {
            obstacleText += " 贝" + shellRemaining;
        }
        if (level.coralReefCount > 0) {
            obstacleText += " 礁" + coralReefRemaining;
        }
        if (level.flowerCount > 0) {
            obstacleText += " 花" + flowerRemaining;
        }
        if (level.keyCount > 0) {
            obstacleText += " 钥" + keyRemaining;
        }
        if (level.countdownBombCount > 0) {
            obstacleText += " 炸" + getCountdownBombRemainingCount() + "/" + getLowestCountdownBombTimer();
        }
        int rewardCellCount = getRewardCellCount();
        if (rewardCellCount > 0) {
            obstacleText += " 奖" + rewardCellCount;
        }
        if (rewardCellClearedCount > 0) {
            // 显示本局已收奖励格数量，和每3格一次的罗盘补给形成清晰目标。
            obstacleText += " 收奖" + rewardCellClearedCount;
        }
        int musicBoxCount = getMusicBoxRemainingCount();
        if (level.musicBoxCount > 0) {
            // 音乐盒是可储备星弦琴的资源点，HUD显示剩余和已收数量。
            obstacleText += " 乐" + musicBoxCount;
            if (lastMusicBoxReward > 0) {
                obstacleText += " 收乐" + lastMusicBoxReward;
            }
        }
        drawTextFitRight(canvas, obstacleText, new RectF(getWidth() * 0.48f, dp(118), getWidth() - dp(18), dp(136)), 15, Color.WHITE);
        textPaint.setTextSize(sp(13));
        String starText = buildStars(getPreviewStars(level));
        if (isEliteLevel()) {
            starText += " 精英";
        }
        if (level.moveLimitGoal > 0) {
            starText += " 挑" + movesUsed + "/" + getMoveLimitGoal(level);
        }
        if (level.comboGoal > 0) {
            starText += " 连" + bestCombo + "/" + level.comboGoal;
        }
        if (level.scoreGoal > 0) {
            starText += " 分" + score / 1000 + "k/" + level.scoreGoal / 1000 + "k";
        }
        if (isHiddenChallengeLevel()) {
            starText += levelHiddenChallengesCleared[levelIndex] ? " 隐✓"
                    : " 隐" + movesUsed + "/" + Math.max(7, level.moves - 4);
        }
        if (levelPerfectCleared[levelIndex]) {
            starText += " 完✓";
        }
        if (comboFeverMoves > 0) {
            starText += " 爆发" + comboFeverMoves;
        }
        if (honeyFreezeMoves > 0) {
            starText += " 冻结" + honeyFreezeMoves;
        }
        if (bombShieldCount > 0) {
            starText += " 护盾" + bombShieldCount;
        }
        drawTextFitRight(canvas, starText, new RectF(getWidth() * 0.34f, dp(142), getWidth() - dp(18), dp(160)), 13, Color.WHITE);
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
        drawBoardAura(canvas, boardRect);
        paint.setColor(Color.argb(120, 255, 255, 255));
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
        drawActivePropPreview(canvas);
        drawPropBar(canvas);
    }

    private void drawBoardAura(Canvas canvas, RectF boardRect) {
        int chapter = getChapterIndex(levelIndex);
        int color = comboFeverMoves > 0 ? Color.rgb(255, 236, 118)
                : (movesLeft <= 5 && !levelComplete && !levelFailed ? Color.rgb(255, 88, 112) : chapterBottomColors[chapter]);
        float pulse = 0.6f + 0.4f * (float) Math.sin(System.currentTimeMillis() / 260.0);
        int alpha = levelComplete ? 115 : (int) (42 + pulse * 40);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(8 + pulse * 5));
        paint.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
        canvas.drawRoundRect(new RectF(boardRect.left - dp(5), boardRect.top - dp(5),
                boardRect.right + dp(5), boardRect.bottom + dp(5)), dp(22), dp(22), paint);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.argb(90, 255, 255, 255));
        for (int i = 0; i < 3; i++) {
            float offset = (System.currentTimeMillis() / (38f + i * 6)) % Math.max(boardRect.width(), 1f);
            float x = boardRect.left + offset;
            // 棋盘环境光给章节和低步数状态更多视觉反馈，不参与玩法计算。
            canvas.drawLine(x, boardRect.top - dp(10), Math.min(boardRect.right, x + dp(42)), boardRect.top - dp(10), paint);
        }
        drawFeverSparks(canvas, boardRect);
        paint.setStyle(Paint.Style.FILL);
        postInvalidateOnAnimation();
    }

    private void drawActivePropPreview(Canvas canvas) {
        if (activeProp == NONE || selectedRow == NONE || selectedCol == NONE) {
            return;
        }
        Set<Cell> cells = buildActivePropPreviewCells(selectedRow, selectedCol);
        if (cells.isEmpty()) {
            return;
        }

        // 点选类道具用棋盘预览标出影响范围，减少误点成本。
        float pulse = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 160.0);
        for (Cell cell : cells) {
            RectF rect = new RectF(boardLeft + cell.col * tileSize + dp(5), boardTop + cell.row * tileSize + dp(5),
                    boardLeft + (cell.col + 1) * tileSize - dp(5), boardTop + (cell.row + 1) * tileSize - dp(5));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb((int) (35 + pulse * 35), 255, 236, 133));
            canvas.drawRoundRect(rect, dp(10), dp(10), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2 + pulse));
            paint.setColor(Color.argb((int) (145 + pulse * 70), 255, 236, 133));
            canvas.drawRoundRect(rect, dp(10), dp(10), paint);
        }
        drawActivePropConfirmMarker(canvas, pulse);
        paint.setStyle(Paint.Style.FILL);
        postInvalidateOnAnimation();
    }

    private void drawActivePropConfirmMarker(Canvas canvas, float pulse) {
        float centerX = boardLeft + selectedCol * tileSize + tileSize / 2f;
        float centerY = boardTop + selectedRow * tileSize + tileSize / 2f;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb((int) (180 + pulse * 55), 33, 37, 56));
        canvas.drawCircle(centerX, centerY, dp(10 + pulse * 3), paint);
        paint.setColor(Color.rgb(255, 236, 133));
        drawPropStar(canvas, centerX, centerY, dp(6 + pulse * 2));
    }

    private Set<Cell> buildActivePropPreviewCells(int row, int col) {
        if (activeProp == PROP_BOMB || activeProp == PROP_CLEANSE || activeProp == PROP_CHAIN_BREAKER) {
            return buildBombCells(row, col);
        } else if (activeProp == PROP_ROW_BLAST) {
            return buildCrossCells(row, col);
        } else if (activeProp == PROP_COLOR_BLAST) {
            return buildColorCells(colorOf(board[row][col]));
        } else if (activeProp == PROP_ROCKET) {
            return buildRocketCells(row, col);
        } else if (activeProp == PROP_LIGHTNING) {
            return buildDiagonalCells(row, col);
        } else if (activeProp == PROP_STAR_COMPASS) {
            return buildStarCompassCells(row, col);
        }
        return buildSingleCell(row, col);
    }

    private void drawFeverSparks(Canvas canvas, RectF boardRect) {
        if (comboFeverMoves <= 0) {
            return;
        }

        long time = System.currentTimeMillis();
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 10; i++) {
            float progress = ((time / (520f + i * 24)) + i * 0.13f) % 1f;
            float x;
            float y;
            if (i % 4 == 0) {
                x = boardRect.left + boardRect.width() * progress;
                y = boardRect.top - dp(12);
            } else if (i % 4 == 1) {
                x = boardRect.right + dp(12);
                y = boardRect.top + boardRect.height() * progress;
            } else if (i % 4 == 2) {
                x = boardRect.right - boardRect.width() * progress;
                y = boardRect.bottom + dp(12);
            } else {
                x = boardRect.left - dp(12);
                y = boardRect.bottom - boardRect.height() * progress;
            }
            int alpha = 120 + (int) (90 * Math.abs(Math.sin(time / 180.0 + i)));
            paint.setColor(Color.argb(alpha, 255, 236, 118));
            canvas.drawCircle(x, y, dp(3 + i % 3), paint);
        }
    }

    private void drawLevelMap(Canvas canvas) {
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(sp(24));
        canvas.drawText("关卡地图 " + (levelMapPage + 1) + "/" + getLevelMapPageCount(), getWidth() / 2f, dp(54), textPaint);
        drawMapChapterBanner(canvas);
        drawDailyChallengeEntry(canvas);
        drawChapterChestEntry(canvas);
        drawChapterProgress(canvas);
        drawAchievementProgress(canvas);
        drawReplayHintEntry(canvas);

        int columns = 6;
        int pageStart = levelMapPage * LEVELS_PER_PAGE;
        int pageCount = Math.min(LEVELS_PER_PAGE, levels.size() - pageStart);
        int rows = (int) Math.ceil(pageCount / (float) columns);
        float gap = dp(6);
        // 地图上方信息区分层摆放，避免推荐、成就和关卡格互相压住。
        float startY = dp(262);
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
            if (isReplayTargetLevel(level)) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(2));
                paint.setColor(Color.argb(230, 255, 236, 133));
                canvas.drawRoundRect(rect, dp(10), dp(10), paint);
                paint.setStyle(Paint.Style.FILL);
                drawReplayTargetMapMark(canvas, level, rect);
            }
            if (unlocked && getLevelRewardCellCount(levels.get(level)) >= 3) {
                drawRewardLevelMapMark(canvas, rect);
            }
            if (levelRanks[level] >= 4) {
                drawHighRankMapGlow(canvas, level, rect);
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
            if (levelRanks[level] > 0) {
                textPaint.setTextSize(sp(8));
                textPaint.setColor(Color.WHITE);
                canvas.drawText(buildRankText(levelRanks[level]), rect.right - dp(10), rect.top + dp(12), textPaint);
            }
            drawLevelTypeMark(canvas, level, rect);
            drawFailAssistMark(canvas, level, rect, unlocked);
            drawFullStarMapMark(canvas, level, rect, unlocked);
            drawPerfectMapMark(canvas, level, rect, unlocked);
        }

        drawLevelMapPager(canvas);
    }

    private void drawRewardLevelMapMark(Canvas canvas, RectF rect) {
        // 地图上标出奖励格密集关，方便玩家回访时快速挑选高收益关卡。
        RectF badge = new RectF(rect.left + dp(4), rect.bottom - dp(18), rect.left + dp(24), rect.bottom - dp(4));
        paint.setColor(Color.argb(220, 255, 225, 92));
        canvas.drawRoundRect(badge, dp(5), dp(5), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(8));
        textPaint.setColor(Color.rgb(33, 37, 56));
        canvas.drawText("奖", badge.centerX(), badge.centerY() + dp(3), textPaint);
    }

    private void drawReplayTargetMapMark(Canvas canvas, int level, RectF rect) {
        // 推荐补星关在地图格上直接标出，方便玩家从章节页快速识别回访目标。
        RectF badge = new RectF(rect.right - dp(24), rect.bottom - dp(18), rect.right - dp(4), rect.bottom - dp(4));
        paint.setColor(Color.argb(225, 255, 236, 133));
        canvas.drawRoundRect(badge, dp(5), dp(5), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(8));
        textPaint.setColor(Color.rgb(33, 37, 56));
        canvas.drawText(buildReplayTargetMapMarkText(level), badge.centerX(), badge.centerY() + dp(3), textPaint);
    }

    private String buildReplayTargetMapMarkText(int level) {
        if (levelStars[level] < 3) {
            return "星";
        }
        Level replayLevel = levels.get(level);
        if (replayLevel.elite && levelRanks[level] < 4) {
            return "精";
        } else if (replayLevel.moveLimitGoal > 0 && levelRanks[level] < 4) {
            return "步";
        } else if (replayLevel.comboGoal > 0 && levelRanks[level] < 4) {
            return "连";
        } else if (replayLevel.scoreGoal > 0 && levelRanks[level] < 4) {
            return "分";
        } else if (isHiddenChallengeLevel(level) && !levelHiddenChallengesCleared[level]) {
            return "隐";
        } else if (levelRanks[level] >= 4 && !levelPerfectCleared[level]) {
            return "完";
        }
        return "评";
    }

    private void drawHighRankMapGlow(Canvas canvas, int level, RectF rect) {
        int alpha = levelRanks[level] >= 6 ? 230 : (levelRanks[level] == 5 ? 190 : 150);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(levelRanks[level] >= 6 ? 2.4f : 1.8f));
        paint.setColor(Color.argb(alpha, 255, 236, 133));
        // 高评级关卡在地图上加亮，强化冲S/SS/SSS后的收藏感。
        canvas.drawRoundRect(new RectF(rect.left + dp(2), rect.top + dp(2), rect.right - dp(2), rect.bottom - dp(2)),
                dp(9), dp(9), paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawFullStarMapMark(Canvas canvas, int level, RectF rect, boolean unlocked) {
        if (!unlocked || levelStars[level] < 3) {
            return;
        }

        // 满星角标让补星成果在地图上更像可收集的勋章。
        paint.setColor(Color.argb(220, 255, 213, 92));
        Path crown = new Path();
        crown.moveTo(rect.right - dp(24), rect.bottom - dp(8));
        crown.lineTo(rect.right - dp(20), rect.bottom - dp(20));
        crown.lineTo(rect.right - dp(15), rect.bottom - dp(13));
        crown.lineTo(rect.right - dp(10), rect.bottom - dp(20));
        crown.lineTo(rect.right - dp(6), rect.bottom - dp(8));
        crown.close();
        canvas.drawPath(crown, paint);
    }

    private void drawPerfectMapMark(Canvas canvas, int level, RectF rect, boolean unlocked) {
        if (!unlocked || !levelPerfectCleared[level]) {
            return;
        }

        // 完美通关记录在地图上常驻显示，强化高手回访后的收藏感。
        RectF badge = new RectF(rect.left + dp(4), rect.top + dp(20), rect.left + dp(26), rect.top + dp(34));
        paint.setColor(Color.argb(225, 116, 219, 214));
        canvas.drawRoundRect(badge, dp(5), dp(5), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(8));
        textPaint.setColor(Color.rgb(33, 37, 56));
        canvas.drawText("完", badge.centerX(), badge.centerY() + dp(3), textPaint);
    }

    private void drawFailAssistMark(Canvas canvas, int level, RectF rect, boolean unlocked) {
        int failStreak = levelFailStreaks[level];
        if (!unlocked || failStreak <= 0) {
            return;
        }

        // 地图角标提示下次开局助力，让卡关补偿在地图上也能被玩家看见。
        RectF badge = new RectF(rect.left + dp(4), rect.top + dp(4), rect.left + dp(26), rect.top + dp(18));
        paint.setColor(Color.argb(215, 255, 236, 133));
        canvas.drawRoundRect(badge, dp(5), dp(5), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(8));
        textPaint.setColor(Color.rgb(33, 37, 56));
        canvas.drawText("助" + Math.min(3, failStreak), badge.centerX(), badge.centerY() + dp(3), textPaint);
    }

    private void drawLevelTypeMark(Canvas canvas, int levelIndex, RectF rect) {
        String mark = buildLevelTypeMark(levelIndex);
        if (mark.length() == 0) {
            return;
        }

        boolean bombLevel = levels.get(levelIndex).countdownBombCount > 0;
        paint.setColor(bombLevel ? Color.argb(215, 255, 88, 112) : Color.argb(190, 33, 37, 56));
        RectF badge = new RectF(rect.left + dp(4), rect.bottom - dp(18), rect.left + dp(22), rect.bottom - dp(4));
        canvas.drawRoundRect(badge, dp(5), dp(5), paint);
        if (bombLevel) {
            // 炸弹关在地图上用高压色标出，方便玩家提前准备时钟/护盾类道具。
            float pulse = 0.55f + 0.45f * (float) Math.sin(System.currentTimeMillis() / 220.0);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1.4f));
            paint.setColor(Color.argb((int) (150 + pulse * 80), 255, 236, 133));
            canvas.drawRoundRect(badge, dp(5), dp(5), paint);
            paint.setStyle(Paint.Style.FILL);
            postInvalidateOnAnimation();
        } else if (hasUnclearedLevelChallenge(levelIndex)) {
            // 未完成挑战目标的关卡加亮描边，方便玩家回头补挑战。
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1.5f));
            paint.setColor(Color.argb(230, 255, 236, 133));
            canvas.drawRoundRect(badge, dp(5), dp(5), paint);
            paint.setStyle(Paint.Style.FILL);
        } else if (isHiddenChallengeLevel(levelIndex) && levelHiddenChallengesCleared[levelIndex]) {
            // 已完成隐藏挑战用冷色描边，和待补挑战形成区分。
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1.4f));
            paint.setColor(Color.argb(220, 116, 219, 214));
            canvas.drawRoundRect(badge, dp(5), dp(5), paint);
            paint.setStyle(Paint.Style.FILL);
        }
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(8));
        textPaint.setColor(Color.WHITE);
        canvas.drawText(mark, badge.centerX(), badge.centerY() + dp(3), textPaint);
    }

    private boolean hasUnclearedLevelChallenge(int levelIndex) {
        if (levelStars[levelIndex] <= 0) {
            return false;
        }
        Level level = levels.get(levelIndex);
        return (level.moveLimitGoal > 0 && levelRanks[levelIndex] < 4)
                || (level.comboGoal > 0 && levelRanks[levelIndex] < 4)
                || (level.scoreGoal > 0 && levelRanks[levelIndex] < 4)
                || (level.elite && levelRanks[levelIndex] < 4)
                || (isHiddenChallengeLevel(levelIndex) && !levelHiddenChallengesCleared[levelIndex]);
    }

    private void drawMapChapterBanner(Canvas canvas) {
        int firstChapter = getFirstChapterOnMapPage();
        int lastChapter = getLastChapterOnMapPage();
        int count = lastChapter - firstChapter + 1;
        float gap = dp(6);
        float width = (getWidth() - dp(56) - gap * (count - 1)) / count;
        float top = dp(58);

        for (int chapter = firstChapter; chapter <= lastChapter; chapter++) {
            float left = dp(28) + (chapter - firstChapter) * (width + gap);
            RectF rect = new RectF(left, top, left + width, top + dp(24));
            int color = chapterBottomColors[chapter];
            paint.setColor(Color.argb(chapter == getCurrentMapChapter() ? 175 : 95,
                    Color.red(color), Color.green(color), Color.blue(color)));
            canvas.drawRoundRect(rect, dp(10), dp(10), paint);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(sp(10));
            textPaint.setColor(Color.WHITE);
            drawTextFit(canvas, chapterNames[chapter] + " " + getChapterStars(chapter) + buildChapterMapBadgeText(chapter),
                    rect, 10, Color.WHITE);
        }
    }

    private String buildChapterMapBadgeText(int chapter) {
        String text = "";
        if (getChapterStars(chapter) >= CHAPTER_SIZE * 3) {
            text += " 满";
        }
        if (getChapterRankScore(chapter) >= getChapterRankRewardTarget()) {
            text += " 评";
        }
        if (getChapterEliteCount(chapter) > 0 && getChapterClearedEliteCount(chapter) >= getChapterEliteCount(chapter)) {
            text += " 精";
        }
        if (getChapterHiddenChallengeCount(chapter) > 0
                && getChapterClearedHiddenChallengeCount(chapter) >= getChapterHiddenChallengeCount(chapter)) {
            // 章节横幅也显示隐藏挑战收集状态，让地图总览更有完成感。
            text += " 隐";
        }
        if (getChapterUnlockedCount(chapter) >= CHAPTER_SIZE
                && getChapterPerfectClearCount(chapter) >= getChapterUnlockedCount(chapter)) {
            text += " 完";
        }
        if (getChapterRewardLevelCount(chapter) > 0
                && getChapterUnlockedRewardLevelCount(chapter) >= getChapterRewardLevelCount(chapter)) {
            // 章节奖励关全部解锁后在横幅展示，方便玩家快速定位高收益章节。
            text += " 奖";
        }
        if (getChapterBombLevelCount(chapter) > 0
                && getChapterUnlockedBombLevelCount(chapter) >= getChapterBombLevelCount(chapter)) {
            // 章节炸弹关全部解锁后也显示，便于提前规划护盾/时钟道具。
            text += " 炸";
        }
        if (getChapterMusicBoxLevelCount(chapter) > 0
                && getChapterUnlockedMusicBoxLevelCount(chapter) >= getChapterMusicBoxLevelCount(chapter)) {
            // 章节音乐盒关全部解锁后显示，强化终章星弦琴补给追求。
            text += " 乐";
        }
        return text;
    }

    private String buildLevelTypeMark(int levelIndex) {
        Level level = levels.get(levelIndex);
        if (isEliteLevel(levelIndex)) {
            return "精";
        } else if (isHiddenChallengeLevel(levelIndex)) {
            return "隐";
        } else if (level.scoreGoal > 0) {
            return "分";
        } else if (level.comboGoal > 0) {
            return "连";
        } else if (level.moveLimitGoal > 0) {
            return "步";
        } else if (level.rainbowArcCount > 0) {
            return "拱";
        } else if (level.crystalCoreCount > 0) {
            return "晶";
        } else if (level.musicBoxCount > 0) {
            return "乐";
        } else if (level.meteorTrailCount > 0) {
            return "航";
        } else if (level.countdownBombCount > 0) {
            return "炸";
        } else if (level.rainbowBottleCount > 0) {
            return "虹";
        } else if (level.energyPotionCount > 0) {
            return "能";
        } else if (level.butterflyCount > 0) {
            return "蝶";
        } else if (level.coralReefCount > 0) {
            return "礁";
        } else if (level.auroraPrismCount > 0) {
            return "棱";
        } else if (level.resonanceDrumCount > 0) {
            return "鼓";
        } else if (level.paintBucketCount > 0) {
            return "染";
        } else if (level.windmillCount > 0) {
            return "风";
        } else if (level.jewelBowCount > 0) {
            return "结";
        } else if (level.stardustJarCount > 0) {
            return "尘";
        } else if (level.wishLampCount > 0) {
            return "愿";
        } else if (level.luckyCloverCount > 0) {
            return "草";
        } else if (level.pearlCount > 0) {
            return "珠";
        } else if (level.carouselCount > 0) {
            return "转";
        } else if (level.ferrisTicketCount > 0) {
            return "票";
        } else if (level.fireworksBarrelCount > 0) {
            return "烟";
        } else if (level.starportBeaconCount > 0) {
            return "港";
        } else if (level.coinPouchCount > 0) {
            return "袋";
        } else if (level.goldenEggCount > 0) {
            return "金";
        } else if (level.luckyStarCount > 0) {
            return "星";
        } else if (level.mysteryBoxCount > 0) {
            return "盒";
        } else if (level.hourglassCount > 0) {
            return "沙";
        } else if (level.portalCount > 0) {
            return "传";
        } else if (level.gemCount > 0) {
            return "钻";
        }
        return "";
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
        dailyChallengeRect.set(dp(28), dp(70), getWidth() / 2f - dp(4), dp(104));
        boolean claimed = prefs.getLong(KEY_DAILY_CHALLENGE_DAY, -1L) == getToday();
        paint.setColor(claimed ? Color.argb(105, 255, 255, 255) : Color.argb(205, 255, 236, 133));
        canvas.drawRoundRect(dailyChallengeRect, dp(14), dp(14), paint);
        if (!claimed) {
            paint.setColor(Color.argb(210, 255, 255, 255));
            float sparkleX = dailyChallengeRect.right - dp(24);
            float sparkleY = dailyChallengeRect.centerY();
            canvas.drawCircle(sparkleX, sparkleY, dp(4 + (System.currentTimeMillis() / 220) % 3), paint);
            postInvalidateOnAnimation();
        }

        String text = claimed ? "每日挑战 再玩" : "每日挑战 奖励";
        if (dailyChallengeStreak > 1) {
            text += " 连" + dailyChallengeStreak;
        }
        text += buildNextDailyChallengeRewardHint();
        drawTextFit(canvas, text, dailyChallengeRect, 12, claimed ? Color.WHITE : Color.rgb(33, 37, 56));
        drawDailyGoalEntry(canvas);
    }

    private String buildNextDailyChallengeRewardHint() {
        int nextStreak = getNextDailyChallengeMilestone();
        int prop = getDailyChallengeMilestoneProp(nextStreak);
        return prop == NONE ? "" : " 到" + nextStreak + "奖" + getPropName(prop)
                + "+" + getDailyChallengeMilestoneAmount(prop);
    }

    private int getNextDailyChallengeMilestone() {
        // 预告最近的每日挑战连胜奖励节点，方便玩家规划下一轮挑战。
        for (int streak = dailyChallengeStreak + 1; streak <= dailyChallengeStreak + 30; streak++) {
            if (getDailyChallengeMilestoneProp(streak) != NONE) {
                return streak;
            }
        }
        return NONE;
    }

    private void drawDailyGoalEntry(Canvas canvas) {
        dailyGoalRect.set(getWidth() / 2f + dp(4), dp(70), getWidth() - dp(28), dp(104));
        boolean claimable = !dailyGoalClaimed && dailyGoalProgress >= 6;
        paint.setColor(claimable ? Color.argb(205, 116, 219, 214) : Color.argb(105, 255, 255, 255));
        canvas.drawRoundRect(dailyGoalRect, dp(14), dp(14), paint);
        if (claimable) {
            paint.setColor(Color.argb(210, 255, 255, 255));
            canvas.drawCircle(dailyGoalRect.right - dp(18), dailyGoalRect.centerY(),
                    dp(4 + (System.currentTimeMillis() / 240) % 3), paint);
            postInvalidateOnAnimation();
        }

        String text = dailyGoalClaimed ? "每日目标 已领"
                : (claimable ? "每日目标 领取" + getDailyGoalRewardText()
                : "每日目标 " + Math.min(6, dailyGoalProgress) + "/6星 " + getDailyGoalRewardText());
        drawTextFit(canvas, text, dailyGoalRect, 12, claimable ? Color.rgb(33, 37, 56) : Color.WHITE);
        drawDailyGoalProgressBar(canvas, dailyGoalRect, claimable);
    }

    private String getDailyGoalRewardText() {
        int prop = getDailyGoalRewardProp();
        return getPropName(prop) + "+" + getDailyGoalRewardAmount(prop);
    }

    private void drawDailyGoalProgressBar(Canvas canvas, RectF rect, boolean claimable) {
        if (dailyGoalClaimed) {
            return;
        }

        // 每日目标进度条让今日星数积累更直观。
        float progress = Math.min(1f, dailyGoalProgress / 6f);
        float left = rect.left + dp(10);
        float right = rect.right - dp(10);
        float top = rect.bottom - dp(7);
        paint.setColor(Color.argb(70, 33, 37, 56));
        canvas.drawRoundRect(new RectF(left, top, right, top + dp(4)), dp(2), dp(2), paint);
        paint.setColor(claimable ? Color.argb(220, 255, 255, 255) : Color.argb(210, 116, 219, 214));
        canvas.drawRoundRect(new RectF(left, top, left + (right - left) * progress, top + dp(4)), dp(2), dp(2), paint);
    }

    private void drawChapterChestEntry(Canvas canvas) {
        int chapter = getCurrentMapChapter();
        chapterChestRect.set(dp(28), dp(112), getWidth() - dp(28), dp(146));
        boolean claimable = canClaimChapterChest(chapter);
        paint.setColor(claimable ? Color.argb(205, 255, 236, 133) : Color.argb(105, 255, 255, 255));
        canvas.drawRoundRect(chapterChestRect, dp(14), dp(14), paint);
        if (claimable) {
            // 章节宝箱可领取时给轻量亮点，提示玩家及时领取。
            paint.setColor(Color.argb(210, 255, 255, 255));
            canvas.drawCircle(chapterChestRect.right - dp(20), chapterChestRect.centerY(),
                    dp(4 + (System.currentTimeMillis() / 240) % 3), paint);
            postInvalidateOnAnimation();
        }

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(14));
        textPaint.setColor(claimable ? Color.rgb(33, 37, 56) : Color.WHITE);
        String text = chapterChestClaimed[chapter] ? chapterNames[chapter] + " 已领"
                : chapterNames[chapter] + " 宝箱 " + getChapterStars(chapter) + "/" + CHAPTER_CHEST_STARS;
        if (claimable) {
            text += " " + getPropName(getChapterChestRewardProp(chapter)) + "+" + getChapterChestRewardAmount(chapter);
        }
        drawTextFit(canvas, text, chapterChestRect, 14, claimable ? Color.rgb(33, 37, 56) : Color.WHITE);
        drawChapterChestProgressBar(canvas, chapter, chapterChestRect, claimable);
    }

    private void drawChapterChestProgressBar(Canvas canvas, int chapter, RectF rect, boolean claimable) {
        if (chapterChestClaimed[chapter]) {
            return;
        }

        // 章节宝箱进度条让整章刷星目标更明确。
        float progress = Math.min(1f, getChapterStars(chapter) / (float) CHAPTER_CHEST_STARS);
        float left = rect.left + dp(12);
        float right = rect.right - dp(12);
        float top = rect.bottom - dp(7);
        paint.setColor(Color.argb(70, 33, 37, 56));
        canvas.drawRoundRect(new RectF(left, top, right, top + dp(4)), dp(2), dp(2), paint);
        paint.setColor(claimable ? Color.argb(220, 33, 37, 56) : Color.argb(210, 255, 236, 133));
        canvas.drawRoundRect(new RectF(left, top, left + (right - left) * progress, top + dp(4)), dp(2), dp(2), paint);
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
        if (getChapterStars(chapter) >= CHAPTER_SIZE * 3) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(Color.argb(230, 255, 236, 133));
            canvas.drawRoundRect(new RectF(left, top, right, top + dp(10)), dp(5), dp(5), paint);
            paint.setStyle(Paint.Style.FILL);
        }

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(12));
        textPaint.setColor(Color.WHITE);
        String status = buildChapterStarGoalHint(chapter);
        String eliteStatus = buildChapterEliteGoalHint(chapter);
        String rankStatus = buildChapterRankGoalHint(chapter);
        String hiddenStatus = buildChapterHiddenProgressText(chapter);
        String perfectStatus = buildChapterPerfectProgressText(chapter);
        RectF progressTextRect = new RectF(left, top + dp(14), right, top + dp(34));
        RectF rankTextRect = new RectF(left, top + dp(28), right, top + dp(50));
        drawTextFit(canvas, "章节进度 " + getChapterUnlockedCount(chapter) + "/" + CHAPTER_SIZE
                + "  星 " + getChapterStars(chapter) + status, progressTextRect, 12, Color.WHITE);
        drawTextFit(canvas, "章节评级 " + getChapterRankScore(chapter) + "/" + getChapterRankRewardTarget() + rankStatus
                        + "  精英 " + getChapterClearedEliteCount(chapter) + "/" + getChapterEliteCount(chapter) + eliteStatus
                        + "  奖励关 " + getChapterUnlockedRewardLevelCount(chapter) + "/" + getChapterRewardLevelCount(chapter)
                        + "  炸弹关 " + getChapterUnlockedBombLevelCount(chapter) + "/" + getChapterBombLevelCount(chapter)
                        + "  音乐盒 " + getChapterUnlockedMusicBoxLevelCount(chapter) + "/" + getChapterMusicBoxLevelCount(chapter)
                        + hiddenStatus + perfectStatus,
                rankTextRect, 12, Color.WHITE);
        if (shouldPulseChapterProgressGoal(chapter)) {
            drawChapterProgressGoalSpark(canvas, right - dp(8), top + dp(5));
        }
    }

    private String buildChapterStarGoalHint(int chapter) {
        int stars = getChapterStars(chapter);
        int fullStarTarget = CHAPTER_SIZE * 3;
        if (stars >= fullStarTarget) {
            return chapterMasteryClaimed[chapter] ? "  大师已领" : "  大师奖励";
        }
        if (stars >= CHAPTER_CHEST_STARS || getChapterUnlockedCount(chapter) >= CHAPTER_SIZE) {
            return "  满星差" + (fullStarTarget - stars);
        }
        return "";
    }

    private String buildChapterRankGoalHint(int chapter) {
        int rankScore = getChapterRankScore(chapter);
        int rankTarget = getChapterRankRewardTarget();
        if (rankScore >= rankTarget) {
            return chapterRankClaimed[chapter] ? " 已领" : " 奖励";
        }
        if (getChapterUnlockedCount(chapter) >= CHAPTER_SIZE / 2) {
            return " 差" + (rankTarget - rankScore);
        }
        return "";
    }

    private String buildChapterEliteGoalHint(int chapter) {
        int eliteCount = getChapterEliteCount(chapter);
        int clearedElite = getChapterClearedEliteCount(chapter);
        if (eliteCount <= 0) {
            return "";
        }
        if (clearedElite >= eliteCount) {
            return chapterEliteClaimed[chapter] ? " 已领" : " 奖励";
        }
        if (clearedElite > 0 || getChapterUnlockedCount(chapter) >= CHAPTER_SIZE) {
            return " 差" + (eliteCount - clearedElite);
        }
        return "";
    }

    private String buildChapterHiddenProgressText(int chapter) {
        int hiddenCount = getChapterHiddenChallengeCount(chapter);
        if (hiddenCount <= 0) {
            return "";
        }

        int clearedHidden = getChapterClearedHiddenChallengeCount(chapter);
        String hint = "";
        if (clearedHidden >= hiddenCount) {
            hint = chapterHiddenClaimed[chapter] ? " 已领" : " 奖励";
        } else if (clearedHidden > 0 || getChapterUnlockedCount(chapter) >= CHAPTER_SIZE) {
            hint = " 差" + (hiddenCount - clearedHidden);
        }
        // 章节页补上隐藏挑战进度，给老关回访多一个可见追求。
        return "  隐藏 " + clearedHidden + "/" + hiddenCount + hint;
    }

    private String buildChapterPerfectProgressText(int chapter) {
        int perfectCount = getChapterPerfectClearCount(chapter);
        if (perfectCount <= 0 && getChapterUnlockedCount(chapter) < CHAPTER_SIZE) {
            return "";
        }
        String hint = "";
        if (perfectCount >= getChapterUnlockedCount(chapter) && getChapterUnlockedCount(chapter) >= CHAPTER_SIZE) {
            hint = chapterPerfectClaimed[chapter] ? " 已领" : " 奖励";
        }
        // 完美进度展示高手向回访目标，并提示整章完美奖励状态。
        return "  完美 " + perfectCount + "/" + getChapterUnlockedCount(chapter) + hint;
    }

    private boolean shouldPulseChapterProgressGoal(int chapter) {
        int stars = getChapterStars(chapter);
        int fullStarMissing = CHAPTER_SIZE * 3 - stars;
        int rankMissing = getChapterRankRewardTarget() - getChapterRankScore(chapter);
        int eliteCount = getChapterEliteCount(chapter);
        int clearedElite = getChapterClearedEliteCount(chapter);
        int eliteMissing = eliteCount - clearedElite;
        int hiddenCount = getChapterHiddenChallengeCount(chapter);
        int clearedHidden = getChapterClearedHiddenChallengeCount(chapter);
        int hiddenMissing = hiddenCount - clearedHidden;
        int unlockedCount = getChapterUnlockedCount(chapter);
        int perfectMissing = unlockedCount - getChapterPerfectClearCount(chapter);
        int musicBoxCount = getChapterMusicBoxLevelCount(chapter);
        int unlockedMusicBox = getChapterUnlockedMusicBoxLevelCount(chapter);
        return (!chapterMasteryClaimed[chapter] && stars >= CHAPTER_CHEST_STARS && fullStarMissing >= 0 && fullStarMissing <= 6)
                || (!chapterRankClaimed[chapter] && unlockedCount >= CHAPTER_SIZE / 2
                && rankMissing >= 0 && rankMissing <= 8)
                || (!chapterEliteClaimed[chapter] && eliteCount > 0
                && (clearedElite > 0 || unlockedCount >= CHAPTER_SIZE)
                && eliteMissing >= 0 && eliteMissing <= 1)
                || (!chapterHiddenClaimed[chapter] && hiddenCount > 0
                && (clearedHidden > 0 || unlockedCount >= CHAPTER_SIZE)
                && hiddenMissing >= 0 && hiddenMissing <= 1)
                || (!chapterPerfectClaimed[chapter] && unlockedCount >= CHAPTER_SIZE
                && perfectMissing >= 0 && perfectMissing <= 1)
                || (musicBoxCount > 0 && unlockedMusicBox > 0 && musicBoxCount - unlockedMusicBox <= 1);
    }

    private void drawChapterProgressGoalSpark(Canvas canvas, float x, float y) {
        // 章节奖励临近时给进度条加一点闪光，提醒玩家回头补星、冲评级、补隐藏和冲完美。
        float pulse = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 180.0);
        paint.setColor(Color.argb((int) (120 + pulse * 85), 255, 255, 255));
        canvas.drawCircle(x, y, dp(3) + pulse * dp(2), paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1));
        paint.setColor(Color.argb((int) (80 + pulse * 70), 255, 236, 133));
        canvas.drawCircle(x, y, dp(6) + pulse * dp(2), paint);
        paint.setStyle(Paint.Style.FILL);
        postInvalidateOnAnimation();
    }

    private void drawAchievementProgress(Canvas canvas) {
        float left = dp(34);
        float top = dp(206);
        float right = getWidth() - dp(34);
        float progress = getClaimedAchievementCount() / (float) ACHIEVEMENT_COUNT;

        paint.setColor(Color.argb(80, 33, 37, 56));
        canvas.drawRoundRect(new RectF(left, top, right, top + dp(8)), dp(4), dp(4), paint);
        paint.setColor(Color.argb(210, 116, 219, 214));
        canvas.drawRoundRect(new RectF(left, top, left + (right - left) * progress, top + dp(8)), dp(4), dp(4), paint);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(11));
        textPaint.setColor(Color.WHITE);
        RectF seasonTextRect = new RectF(left, top + dp(12), right, top + dp(30));
        drawTextFit(canvas, "成就 " + getClaimedAchievementCount() + "/" + ACHIEVEMENT_COUNT
                + buildNextAchievementRewardHint() + "  评级 " + getTotalRankScore()
                + "  完美 " + getTotalPerfectClearCount() + "/" + getUnlockedLevelCount()
                + "  赛季 " + seasonLevels + "/" + getNextSeasonLevelTarget()
                + "关 " + seasonStars + "/" + getNextSeasonStarTarget() + "星" + buildNextSeasonRewardHint(),
                seasonTextRect, 11, Color.WHITE);
        drawSeasonProgressBar(canvas, left, top + dp(30), right);
    }

    private String buildNextAchievementRewardHint() {
        int index = getNextAchievementRewardIndex();
        int prop = getAchievementRewardProp(index);
        return prop == NONE ? "" : " 到" + (index + 1) + "成就奖" + getPropName(prop)
                + "+" + getAchievementRewardAmount(index);
    }

    private int getNextAchievementRewardIndex() {
        // 成就道具从中后期开始出现，预告最近节点提升长期追求感。
        for (int index = 0; index < ACHIEVEMENT_COUNT; index++) {
            if (!achievementsClaimed[index] && getAchievementRewardProp(index) != NONE) {
                return index;
            }
        }
        return NONE;
    }

    private String buildNextSeasonRewardHint() {
        int nextStep = getNextSeasonRewardStep();
        int prop = getSeasonRewardProp(nextStep);
        return prop == NONE ? "" : " 到" + nextStep + "季奖" + getPropName(prop)
                + "+" + getSeasonRewardAmount(prop);
    }

    private int getNextSeasonRewardStep() {
        // 赛季道具不是每档都有，预告最近奖励档位让长线目标更明确。
        for (int step = seasonRewardStep + 1; step <= seasonRewardStep + 10; step++) {
            if (getSeasonRewardProp(step) != NONE) {
                return step;
            }
        }
        return NONE;
    }

    private void drawSeasonProgressBar(Canvas canvas, float left, float top, float right) {
        float levelProgress = seasonLevels / (float) Math.max(1, getNextSeasonLevelTarget());
        float starProgress = seasonStars / (float) Math.max(1, getNextSeasonStarTarget());
        float progress = Math.min(1f, Math.max(levelProgress, starProgress));
        // 赛季进度用较亮进度条呈现，便于判断下一阶段奖励距离。
        paint.setColor(Color.argb(70, 33, 37, 56));
        canvas.drawRoundRect(new RectF(left, top, right, top + dp(6)), dp(3), dp(3), paint);
        paint.setColor(Color.argb(210, 255, 213, 92));
        canvas.drawRoundRect(new RectF(left, top, left + (right - left) * progress, top + dp(6)), dp(3), dp(3), paint);
    }

    private void drawReplayHintEntry(Canvas canvas) {
        replayHintRect.set(dp(28), dp(232), getWidth() - dp(28), dp(256));
        int replayLevel = findReplayTargetLevel();
        paint.setColor(replayLevel >= 0 ? Color.argb(120, 255, 236, 133) : Color.argb(70, 255, 255, 255));
        canvas.drawRoundRect(replayHintRect, dp(10), dp(10), paint);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(11));
        textPaint.setColor(Color.WHITE);
        String text = replayLevel >= 0 ? "智能推荐 " + chapterNames[getChapterIndex(replayLevel)]
                + " 第" + (replayLevel + 1) + "关  " + buildReplayReason(replayLevel)
                : "已通关卡暂无补星目标";
        drawTextFit(canvas, text, replayHintRect, 11, Color.WHITE);
    }

    private void drawTextFit(Canvas canvas, String text, RectF rect, float baseSize, int color) {
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(baseSize));
        textPaint.setColor(color);
        float maxWidth = Math.max(dp(20), rect.width() - dp(8));
        while (baseSize > 8 && textPaint.measureText(text) > maxWidth) {
            baseSize -= 1;
            textPaint.setTextSize(sp(baseSize));
        }
        canvas.drawText(text, rect.centerX(), rect.centerY() + dp(4), textPaint);
    }

    private void drawTextFitRight(Canvas canvas, String text, RectF rect, float baseSize, int color) {
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setTextSize(sp(baseSize));
        textPaint.setColor(color);
        float maxWidth = Math.max(dp(20), rect.width() - dp(8));
        while (baseSize > 8 && textPaint.measureText(text) > maxWidth) {
            baseSize -= 1;
            textPaint.setTextSize(sp(baseSize));
        }
        canvas.drawText(text, rect.right - dp(4), rect.centerY() + dp(4), textPaint);
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
        starChestRect.set(getWidth() / 2f - dp(96), top, getWidth() / 2f - dp(4), top + dp(38));
        rankChestRect.set(getWidth() / 2f + dp(4), top, getWidth() / 2f + dp(96), top + dp(38));

        paint.setColor(levelMapPage > 0 ? Color.argb(150, 255, 255, 255) : Color.argb(55, 255, 255, 255));
        canvas.drawRoundRect(prevPageRect, dp(14), dp(14), paint);
        paint.setColor(getAvailableStarChests() > 0 ? Color.argb(205, 255, 236, 133) : Color.argb(105, 255, 255, 255));
        canvas.drawRoundRect(starChestRect, dp(14), dp(14), paint);
        paint.setColor(getAvailableRankChests() > 0 ? Color.argb(205, 116, 219, 214) : Color.argb(105, 255, 255, 255));
        canvas.drawRoundRect(rankChestRect, dp(14), dp(14), paint);
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
        drawTextFit(canvas, buildStarChestLabel(), starChestRect, 12, Color.rgb(33, 37, 56));
        drawTextFit(canvas, buildRankChestLabel(), rankChestRect, 12, Color.rgb(33, 37, 56));
        drawMapChestProgressBar(canvas, starChestRect, getTotalStars(), getNextStarChestTarget(), getAvailableStarChests() > 0);
        drawMapChestProgressBar(canvas, rankChestRect, getTotalRankScore(), getNextRankChestTarget(), getAvailableRankChests() > 0);
        if (getAvailableStarChests() > 0) {
            drawClaimableMapChestSpark(canvas, starChestRect);
        }
        if (getAvailableRankChests() > 0) {
            drawClaimableMapChestSpark(canvas, rankChestRect);
        }
        drawStarChestNotice(canvas, top);
    }

    private void drawClaimableMapChestSpark(Canvas canvas, RectF rect) {
        // 底部宝箱可领取时用呼吸光点提示，避免奖励入口被翻页按钮弱化。
        float pulse = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 170.0);
        float x = rect.right - dp(11);
        float y = rect.top + dp(9);
        paint.setColor(Color.argb((int) (130 + pulse * 80), 255, 255, 255));
        canvas.drawCircle(x, y, dp(3) + pulse * dp(2), paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1));
        paint.setColor(Color.argb((int) (85 + pulse * 75), 255, 255, 255));
        canvas.drawCircle(x, y, dp(6) + pulse * dp(2), paint);
        paint.setStyle(Paint.Style.FILL);
        postInvalidateOnAnimation();
    }

    private void drawMapChestProgressBar(Canvas canvas, RectF rect, int current, int target, boolean claimable) {
        if (claimable) {
            return;
        }

        // 底部宝箱进度条提示距离下一次可领取还差多少。
        float progress = Math.min(1f, current / (float) Math.max(1, target));
        float left = rect.left + dp(8);
        float right = rect.right - dp(8);
        float top = rect.bottom - dp(7);
        paint.setColor(Color.argb(70, 33, 37, 56));
        canvas.drawRoundRect(new RectF(left, top, right, top + dp(4)), dp(2), dp(2), paint);
        paint.setColor(Color.argb(190, 255, 236, 133));
        canvas.drawRoundRect(new RectF(left, top, left + (right - left) * progress, top + dp(4)), dp(2), dp(2), paint);
    }

    private int getLevelMapPageCount() {
        return (int) Math.ceil(levels.size() / (float) LEVELS_PER_PAGE);
    }

    private String buildStarChestLabel() {
        if (getAvailableStarChests() > 0) {
            return "宝箱+" + (25 + (starChestClaimed + 1) * 5) + buildNextStarChestPropHint();
        }
        return "星 " + getTotalStars() + "/" + getNextStarChestTarget() + buildNextStarChestPropHint();
    }

    private String buildRankChestLabel() {
        if (getAvailableRankChests() > 0) {
            return "评级+" + (40 + (rankChestClaimed + 1) * 8) + buildNextRankChestPropHint();
        }
        return "评 " + getTotalRankScore() + "/" + getNextRankChestTarget() + buildNextRankChestPropHint();
    }

    private String buildNextStarChestPropHint() {
        int nextChest = getNextStarChestPropStep();
        int prop = getStarChestRewardProp(nextChest);
        return prop == NONE ? "" : " 到" + nextChest + "箱" + getPropName(prop)
                + "+" + getStarChestRewardAmount(prop);
    }

    private String buildNextRankChestPropHint() {
        int nextChest = getNextRankChestPropStep();
        int prop = getRankChestRewardProp(nextChest);
        return prop == NONE ? "" : " 到" + nextChest + "箱" + getPropName(prop)
                + "+" + getRankChestRewardAmount(prop);
    }

    private int getNextStarChestPropStep() {
        // 宝箱道具节点间隔较长，显示最近节点能强化补星追求。
        for (int claimed = starChestClaimed + 1; claimed <= starChestClaimed + 16; claimed++) {
            if (getStarChestRewardProp(claimed) != NONE) {
                return claimed;
            }
        }
        return NONE;
    }

    private int getNextRankChestPropStep() {
        // 评级宝箱同样预告最近道具节点，引导玩家重玩冲高评级。
        for (int claimed = rankChestClaimed + 1; claimed <= rankChestClaimed + 8; claimed++) {
            if (getRankChestRewardProp(claimed) != NONE) {
                return claimed;
            }
        }
        return NONE;
    }

    private void drawStarChestNotice(Canvas canvas, float pagerTop) {
        if (System.currentTimeMillis() > chestNoticeUntilTime) {
            return;
        }

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(13));
        textPaint.setColor(Color.WHITE);
        String text = lastChestReward > 0 ? "星级宝箱 金币+" + lastChestReward + buildChestPropRewardText()
                : buildChestNoticeFallback();
        if (lastChapterChestReward > 0) {
            text = "章节宝箱 金币+" + lastChapterChestReward + buildChestPropRewardText();
        } else if (lastRankChestReward > 0) {
            text = "评级宝箱 金币+" + lastRankChestReward + buildChestPropRewardText();
        } else if (lastDailyGoalReward > 0) {
            text = "每日目标 金币+" + lastDailyGoalReward + buildChestPropRewardText();
        }
        canvas.drawText(text, getWidth() / 2f, pagerTop - dp(10), textPaint);
        postInvalidateOnAnimation();
    }

    private String buildChestNoticeFallback() {
        if (lastChestNoticeType == 2) {
            return "还差 " + Math.max(0, getNextRankChestTarget() - getTotalRankScore()) + " 评级";
        } else if (lastChestNoticeType == 3) {
            int chapter = getCurrentMapChapter();
            return "还差 " + Math.max(0, CHAPTER_CHEST_STARS - getChapterStars(chapter)) + " 章星";
        } else if (lastChestNoticeType == 4) {
            return dailyGoalClaimed ? "每日目标已领取" : "还差 " + Math.max(0, 6 - dailyGoalProgress) + " 今日星";
        }
        return "还差 " + Math.max(0, getNextStarChestTarget() - getTotalStars()) + " 星";
    }

    private String buildChestPropRewardText() {
        return lastChestRewardProp == NONE ? "" : " " + getPropName(lastChestRewardProp) + "+" + lastChestRewardAmount;
    }

    private int getAvailableStarChests() {
        return Math.max(0, getTotalStars() / STAR_CHEST_STEP - starChestClaimed);
    }

    private int getAvailableRankChests() {
        return Math.max(0, getTotalRankScore() / RANK_CHEST_STEP - rankChestClaimed);
    }

    private int getNextStarChestTarget() {
        return Math.min(LEVEL_COUNT * 3, (starChestClaimed + 1) * STAR_CHEST_STEP);
    }

    private int getNextRankChestTarget() {
        return Math.min(LEVEL_COUNT * 6, (rankChestClaimed + 1) * RANK_CHEST_STEP);
    }

    private int getTotalStars() {
        int total = 0;
        for (int i = 0; i < levels.size(); i++) {
            total += levelStars[i];
        }
        return total;
    }

    private int getTotalRankScore() {
        int total = 0;
        for (int i = 0; i < levels.size(); i++) {
            total += levelRanks[i];
        }
        return total;
    }

    private int getTotalPerfectClearCount() {
        int total = 0;
        for (int i = 0; i < levels.size(); i++) {
            if (levelPerfectCleared[i]) {
                total++;
            }
        }
        return total;
    }

    private int getUnlockedLevelCount() {
        return Math.min(levels.size(), highestUnlockedLevel + 1);
    }

    private int getClaimedAchievementCount() {
        int count = 0;
        for (boolean claimed : achievementsClaimed) {
            if (claimed) {
                count++;
            }
        }
        return count;
    }

    private int getNextSeasonLevelTarget() {
        return (seasonRewardStep + 1) * 8;
    }

    private int getNextSeasonStarTarget() {
        return (seasonRewardStep + 1) * 22;
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

    private int getChapterRankScore(int chapter) {
        int total = 0;
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        for (int level = start; level < end; level++) {
            total += levelRanks[level];
        }
        return total;
    }

    private int getChapterEliteCount(int chapter) {
        int total = 0;
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        for (int level = start; level < end; level++) {
            if (isEliteLevel(level)) {
                total++;
            }
        }
        return total;
    }

    private int getChapterClearedEliteCount(int chapter) {
        int total = 0;
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        for (int level = start; level < end; level++) {
            if (isEliteLevel(level) && levelStars[level] > 0) {
                total++;
            }
        }
        return total;
    }

    private int getChapterHiddenChallengeCount(int chapter) {
        int total = 0;
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        for (int level = start; level < end; level++) {
            if (isHiddenChallengeLevel(level)) {
                total++;
            }
        }
        return total;
    }

    private int getChapterClearedHiddenChallengeCount(int chapter) {
        int total = 0;
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        for (int level = start; level < end; level++) {
            if (isHiddenChallengeLevel(level) && levelHiddenChallengesCleared[level]) {
                total++;
            }
        }
        return total;
    }

    private int getChapterPerfectClearCount(int chapter) {
        int total = 0;
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        for (int level = start; level < end; level++) {
            if (levelPerfectCleared[level]) {
                total++;
            }
        }
        return total;
    }

    private int getTotalClearedEliteCount() {
        int total = 0;
        for (int chapter = 0; chapter < chapterNames.length; chapter++) {
            total += getChapterClearedEliteCount(chapter);
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

    private int getChapterRewardLevelCount(int chapter) {
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        int count = 0;
        for (int level = start; level < end; level++) {
            if (getLevelRewardCellCount(levels.get(level)) >= 3) {
                count++;
            }
        }
        return count;
    }

    private int getChapterUnlockedRewardLevelCount(int chapter) {
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        int count = 0;
        for (int level = start; level < end; level++) {
            if (level <= highestUnlockedLevel && getLevelRewardCellCount(levels.get(level)) >= 3) {
                count++;
            }
        }
        return count;
    }

    private int getChapterBombLevelCount(int chapter) {
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        int count = 0;
        for (int level = start; level < end; level++) {
            if (levels.get(level).countdownBombCount > 0) {
                count++;
            }
        }
        return count;
    }

    private int getChapterUnlockedBombLevelCount(int chapter) {
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        int count = 0;
        for (int level = start; level < end; level++) {
            if (level <= highestUnlockedLevel && levels.get(level).countdownBombCount > 0) {
                count++;
            }
        }
        return count;
    }

    private int getChapterMusicBoxLevelCount(int chapter) {
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        int count = 0;
        for (int level = start; level < end; level++) {
            if (levels.get(level).musicBoxCount > 0) {
                count++;
            }
        }
        return count;
    }

    private int getChapterUnlockedMusicBoxLevelCount(int chapter) {
        int start = chapter * CHAPTER_SIZE;
        int end = Math.min(start + CHAPTER_SIZE, levels.size());
        int count = 0;
        for (int level = start; level < end; level++) {
            if (level <= highestUnlockedLevel && levels.get(level).musicBoxCount > 0) {
                count++;
            }
        }
        return count;
    }

    private int findReplayTargetLevel() {
        int bestLevel = NONE;
        int bestScore = 0;
        for (int level = 0; level <= highestUnlockedLevel && level < levels.size(); level++) {
            if (isReplayTargetLevel(level)) {
                int score = getReplayPriorityScore(level);
                if (score > bestScore) {
                    bestLevel = level;
                    bestScore = score;
                }
            }
        }
        return bestLevel;
    }

    private boolean isReplayTargetLevel(int level) {
        return level <= highestUnlockedLevel && levelStars[level] > 0
                && (levelStars[level] < 3 || levelRanks[level] < 4
                || (isHiddenChallengeLevel(level) && !levelHiddenChallengesCleared[level])
                || (levelRanks[level] >= 4 && !levelPerfectCleared[level])
                // 已完美的音乐盒关仍可作为星弦琴补给点，给后期保留资源型回访目标。
                || (levelPerfectCleared[level] && levels.get(level).musicBoxCount > 0));
    }

    private int getReplayPriorityScore(int level) {
        int missingStars = Math.max(0, 3 - levelStars[level]);
        int missingRank = Math.max(0, 4 - levelRanks[level]);
        int chapter = getChapterIndex(level);
        int chapterMissingStars = CHAPTER_CHEST_STARS - getChapterStars(chapter);
        int chapterMissingPerfect = getChapterUnlockedCount(chapter) - getChapterPerfectClearCount(chapter);
        int score = missingStars * 45 + missingRank * 12;
        if (hasUnclearedLevelChallenge(level)) {
            // 补挑战能同时提升评级和章节奖励进度，推荐优先级略高。
            score += 24;
        }
        if (isEliteLevel(level)) {
            score += 18;
        }
        if (isHiddenChallengeLevel(level)) {
            score += 12;
        }
        if (isHiddenChallengeLevel(level) && !levelHiddenChallengesCleared[level]) {
            // 隐藏挑战有独立章节奖励，未完成时提高回访推荐优先级。
            score += 22;
        }
        if (levelRanks[level] >= 4 && !levelPerfectCleared[level]) {
            // 已达高评级后继续推荐冲完美，给高手回访目标。
            score += 10;
        }
        if (!chapterPerfectClaimed[chapter] && getChapterUnlockedCount(chapter) >= CHAPTER_SIZE
                && chapterMissingPerfect > 0 && chapterMissingPerfect <= 3 && !levelPerfectCleared[level]) {
            // 章节完美奖励临近时优先推荐差的几关。
            score += 18;
        }
        if (chapterMissingStars > 0 && chapterMissingStars <= 6) {
            score += 20;
        }
        if (getLevelRewardCellCount(levels.get(level)) >= 3) {
            // 奖励格多的未完成关更适合回访，补评级时也能顺手拿局内补给。
            score += 8;
        }
        if (levels.get(level).countdownBombCount > 0) {
            // 炸弹关回访可以顺手拿护盾补给，略微提高推荐优先级。
            score += 9;
        }
        if (levels.get(level).musicBoxCount > 0) {
            // 音乐盒关能稳定补星弦琴，补评级时更值得被推荐。
            score += 10;
        }
        return score;
    }

    private String buildReplayReason(int level) {
        if (levelStars[level] < 3) {
            return "差" + (3 - levelStars[level]) + "星";
        }
        String challenge = buildReplayChallengeReason(level);
        if (challenge.length() > 0) {
            return challenge;
        }
        if (levelRanks[level] >= 4 && !levelPerfectCleared[level]) {
            return "冲完美";
        }
        if (levels.get(level).countdownBombCount > 0) {
            return "拆弹拿护盾";
        }
        if (levels.get(level).musicBoxCount > 0) {
            return "刷音乐盒储星弦琴";
        }
        if (getLevelRewardCellCount(levels.get(level)) >= 3) {
            return "收奖励格冲评级";
        }
        return "冲" + buildRankText(4);
    }

    private String buildReplayChallengeReason(int levelIndex) {
        Level level = levels.get(levelIndex);
        if (level.elite && levelRanks[levelIndex] < 4) {
            return "补精英评级";
        } else if (level.moveLimitGoal > 0 && levelRanks[levelIndex] < 4) {
            return "补限步" + level.moveLimitGoal + "步";
        } else if (level.comboGoal > 0 && levelRanks[levelIndex] < 4) {
            return "补连击挑战";
        } else if (level.scoreGoal > 0 && levelRanks[levelIndex] < 4) {
            return "补高分挑战";
        } else if (isHiddenChallengeLevel(levelIndex) && !levelHiddenChallengesCleared[levelIndex]) {
            return "补隐藏" + Math.max(7, level.moves - 4) + "步";
        }
        return "";
    }

    private void focusReplayLevel() {
        int level = findReplayTargetLevel();
        if (level < 0) {
            return;
        }

        // 智能补星推荐优先选择高收益关卡，减少地图翻找成本。
        levelMapPage = level / LEVELS_PER_PAGE;
    }

    private void startReplayTargetLevel() {
        int level = findReplayTargetLevel();
        if (level < 0) {
            return;
        }

        // 智能推荐直接进入关卡，让补星和冲评级少一步地图操作。
        showingLevelMap = false;
        startLevel(level);
    }

    private int getCurrentMapChapter() {
        int firstChapter = getFirstChapterOnMapPage();
        int lastChapter = getLastChapterOnMapPage();
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

    private int getFirstChapterOnMapPage() {
        return levelMapPage * LEVELS_PER_PAGE / CHAPTER_SIZE;
    }

    private int getLastChapterOnMapPage() {
        return Math.min(chapterNames.length - 1,
                (levelMapPage * LEVELS_PER_PAGE + LEVELS_PER_PAGE - 1) / CHAPTER_SIZE);
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
            float pulse = 0.72f + 0.28f * (float) Math.sin(System.currentTimeMillis() / 120.0);
            paint.setColor(Color.argb((int) (70 + pulse * 55), 255, 255, 255));
            canvas.drawRoundRect(new RectF(rect.left - dp(4), rect.top - dp(4),
                    rect.right + dp(4), rect.bottom + dp(4)), dp(16), dp(16), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(4));
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(rect, dp(14), dp(14), paint);
            paint.setStyle(Paint.Style.FILL);
            postInvalidateOnAnimation();
        }

        if (isHintCell(row, col)) {
            drawHintBeacon(canvas, rect, centerX, centerY);
            postInvalidateOnAnimation();
        }

        if (hasRewardCell(row, col)) {
            drawRewardCellPulse(canvas, rect);
        }
        drawSpecialGlow(canvas, specialOf(piece), rect, centerX, centerY);
        drawTileIcon(canvas, colorOf(piece), centerX, centerY);
        drawSpecialMark(canvas, specialOf(piece), centerX, centerY);
        drawHoney(canvas, row, col, rect);
        drawIce(canvas, row, col, rect);
        drawStone(canvas, row, col, rect);
        drawVine(canvas, row, col, rect);
        drawGift(canvas, row, col, rect);
        drawChain(canvas, row, col, rect);
        drawShell(canvas, row, col, rect);
        drawCoralReef(canvas, row, col, rect);
        drawFlower(canvas, row, col, rect);
        drawCloud(canvas, row, col, rect);
        drawGem(canvas, row, col, rect);
        drawGoldenEgg(canvas, row, col, rect);
        drawCoinPouch(canvas, row, col, rect);
        drawPaintBucket(canvas, row, col, rect);
        drawWindmill(canvas, row, col, rect);
        drawJewelBow(canvas, row, col, rect);
        drawStardustJar(canvas, row, col, rect);
        drawWishLamp(canvas, row, col, rect);
        drawResonanceDrum(canvas, row, col, rect);
        drawAuroraPrism(canvas, row, col, rect);
        drawRainbowBottle(canvas, row, col, rect);
        drawEnergyPotion(canvas, row, col, rect);
        drawButterfly(canvas, row, col, rect);
        drawPortal(canvas, row, col, rect);
        drawHourglass(canvas, row, col, rect);
        drawLuckyStar(canvas, row, col, rect);
        drawLuckyClover(canvas, row, col, rect);
        drawMysteryBox(canvas, row, col, rect);
        drawPearl(canvas, row, col, rect);
        drawCarousel(canvas, row, col, rect);
        drawFerrisTicket(canvas, row, col, rect);
        drawFireworksBarrel(canvas, row, col, rect);
        drawStarportBeacon(canvas, row, col, rect);
        drawMeteorTrail(canvas, row, col, rect);
        drawRainbowArc(canvas, row, col, rect);
        drawCrystalCore(canvas, row, col, rect);
        drawMusicBox(canvas, row, col, rect);
        drawCountdownBomb(canvas, row, col, rect);
        drawKey(canvas, row, col, rect);
        drawMoveChest(canvas, row, col, rect);
    }

    private void drawSpecialGlow(Canvas canvas, int special, RectF rect, float centerX, float centerY) {
        if (special == SPECIAL_NORMAL) {
            return;
        }

        // 特效棋持续发光，方便玩家在复杂棋盘里快速识别可连锁目标。
        float pulse = 0.65f + 0.35f * (float) Math.sin(System.currentTimeMillis() / 180.0);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2 + pulse * 2));
        paint.setColor(Color.argb((int) (105 + pulse * 80), 255, 255, 255));
        canvas.drawRoundRect(new RectF(rect.left + dp(2), rect.top + dp(2),
                rect.right - dp(2), rect.bottom - dp(2)), dp(12), dp(12), paint);
        if (special == SPECIAL_RAINBOW || special == SPECIAL_BOMB) {
            paint.setColor(Color.argb((int) (95 + pulse * 75), 255, 236, 118));
            canvas.drawCircle(centerX, centerY, tileSize * (0.31f + pulse * 0.04f), paint);
        }
        paint.setStyle(Paint.Style.FILL);
        postInvalidateOnAnimation();
    }

    private void drawRewardCellPulse(Canvas canvas, RectF rect) {
        // 奖励格用金色脉冲描边提示，避免在复杂棋盘里被普通棋子盖过辨识度。
        float pulse = 0.58f + 0.42f * (float) Math.sin(System.currentTimeMillis() / 210.0);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2 + pulse * 1.5f));
        paint.setColor(Color.argb((int) (95 + pulse * 85), 255, 225, 92));
        canvas.drawRoundRect(new RectF(rect.left + dp(3), rect.top + dp(3),
                rect.right - dp(3), rect.bottom - dp(3)), dp(12), dp(12), paint);
        paint.setStyle(Paint.Style.FILL);
        postInvalidateOnAnimation();
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
        paint.setColor(comboFeverMoves > 0 ? Color.argb(235, 255, 159, 64)
                : (comboEnergy >= 80 ? Color.argb(235, 255, 244, 170) : Color.argb(210, 255, 236, 133)));
        RectF fill = new RectF(left, top, left + width * progress, top + dp(8));
        canvas.drawRoundRect(fill, dp(4), dp(4), paint);
        if (comboEnergy >= 80) {
            paint.setColor(Color.argb(150, 255, 255, 255));
            canvas.drawCircle(fill.right, fill.centerY(), dp(4), paint);
        }
        if (comboFeverMoves > 0) {
            textPaint.setTextAlign(Paint.Align.RIGHT);
            textPaint.setTextSize(sp(10));
            textPaint.setColor(Color.WHITE);
            canvas.drawText("爆发x1.25 " + comboFeverMoves, left + width, top - dp(3), textPaint);
        }
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

    private void drawHintBeacon(Canvas canvas, RectF rect, float centerX, float centerY) {
        float pulse = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() / 150.0);
        paint.setColor(Color.argb((int) (45 + pulse * 55), 255, 244, 170));
        canvas.drawCircle(centerX, centerY, tileSize * (0.36f + pulse * 0.08f), paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3 + pulse * 2));
        paint.setColor(Color.rgb(255, 244, 170));
        canvas.drawRoundRect(rect, dp(14), dp(14), paint);
        paint.setStyle(Paint.Style.FILL);
        // 提示光标用一颗小星点强调“下一步”，只参与表现不影响棋盘。
        drawPropStar(canvas, centerX, rect.top - dp(5), dp(6 + pulse * 2));
    }

    private void drawPropBar(Canvas canvas) {
        float top = boardTop + tileSize * BOARD_SIZE + dp(18);
        float gap = dp(6);
        int columns = PROP_COUNT > 20 ? 5 : (PROP_COUNT > 10 ? 4 : (PROP_COUNT > 9 ? 5 : PROP_COUNT));
        float buttonWidth = (getWidth() - dp(32) - gap * (columns - 1)) / columns;
        for (int prop = 0; prop < PROP_COUNT; prop++) {
            int row = prop / columns;
            int col = prop % columns;
            float left = dp(16) + col * (buttonWidth + gap);
            float rowTop = top + row * dp(54);
            RectF rect = new RectF(left, rowTop, left + buttonWidth, rowTop + dp(48));
            propRects[prop] = rect;

            boolean recommended = isRecommendedPropForLevel(prop);
            paint.setColor(activeProp == prop ? Color.argb(235, 255, 255, 255) : Color.argb(120, 255, 255, 255));
            canvas.drawRoundRect(rect, dp(12), dp(12), paint);
            if (recommended) {
                drawRecommendedPropGlow(canvas, rect);
            }

            paint.setColor(Color.argb(propInventory[prop] > 0 ? 220 : 85, 33, 37, 56));
            drawPropIcon(canvas, prop, rect.centerX(), rect.centerY() - dp(6));
            if (recommended) {
                drawRecommendedPropBadge(canvas, rect);
            }

            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(sp(9));
            textPaint.setColor(Color.WHITE);
            String label = propInventory[prop] > 0
                    ? getPropName(prop) + " x" + propInventory[prop]
                    : buildPropPurchaseLabel(prop);
            if (propReserve[prop] > 0) {
                drawReservePropBadge(canvas, rect, propReserve[prop]);
            }
            int labelColor = propInventory[prop] <= 0 && coins < PROP_COSTS[prop]
                    ? Color.rgb(255, 236, 133) : Color.WHITE;
            drawTextFit(canvas, label, new RectF(rect.left + dp(2), rect.bottom - dp(16), rect.right - dp(2),
                    rect.bottom - dp(2)), 9, labelColor);
        }
        drawRecommendedPropTip(canvas, top);
    }

    private String buildPropPurchaseLabel(int prop) {
        if (coins < PROP_COSTS[prop]) {
            return getPropName(prop) + " 差" + (PROP_COSTS[prop] - coins) + "币";
        }
        return getPropName(prop) + " " + PROP_COSTS[prop] + "币";
    }

    private void drawRecommendedPropTip(Canvas canvas, float propBarTop) {
        if (activeProp != NONE) {
            drawActivePropTip(canvas, propBarTop);
            return;
        }

        int prop = getFirstRecommendedProp();
        if (prop == NONE) {
            return;
        }

        // 道具栏推荐给出文字解释，帮助玩家理解当前局面的优先选择。
        RectF rect = new RectF(dp(18), propBarTop - dp(20), getWidth() - dp(18), propBarTop - dp(4));
        paint.setColor(Color.argb(92, 33, 37, 56));
        canvas.drawRoundRect(rect, dp(8), dp(8), paint);
        drawTextFit(canvas, buildRecommendedPropTip(prop), rect, 10, Color.WHITE);
    }

    private void drawActivePropTip(Canvas canvas, float propBarTop) {
        RectF rect = new RectF(dp(18), propBarTop - dp(22), getWidth() - dp(18), propBarTop - dp(4));
        paint.setColor(Color.argb(118, 33, 37, 56));
        canvas.drawRoundRect(rect, dp(8), dp(8), paint);
        drawTextFit(canvas, buildActivePropTip(), rect, 10, Color.rgb(255, 236, 133));
    }

    private String buildActivePropTip() {
        String name = getPropName(activeProp);
        String previewText = buildActivePropPreviewCountText();
        String prefix = selectedRow == NONE ? name + " 先点棋盘预览 " : name + " 再点同格确认" + previewText + " ";
        if (activeProp == PROP_HAMMER) {
            return prefix + "单格清除";
        } else if (activeProp == PROP_BOMB) {
            return prefix + "中心爆破";
        } else if (activeProp == PROP_ROW_BLAST) {
            return prefix + "清十字";
        } else if (activeProp == PROP_COLOR_BLAST) {
            return prefix + "同色全清";
        } else if (activeProp == PROP_ROCKET) {
            return prefix + "发射行列";
        } else if (activeProp == PROP_LIGHTNING) {
            return prefix + "劈开对角线";
        } else if (activeProp == PROP_STAR_COMPASS) {
            return prefix + "释放星轨";
        } else if (activeProp == PROP_TARGET_BRUSH) {
            return prefix + "染成目标色";
        } else if (activeProp == PROP_MAGIC_WAND) {
            return prefix + "升级彩虹棋";
        } else if (activeProp == PROP_BRUSH) {
            return prefix + "升级方向特效";
        } else if (activeProp == PROP_STAR_HAMMER) {
            return prefix + "升级爆炸棋";
        } else if (activeProp == PROP_CLEANSE) {
            return prefix + "净化周围障碍";
        } else if (activeProp == PROP_CHAIN_BREAKER) {
            return prefix + "剪开锁链藤蔓";
        }
        return prefix + "使用";
    }

    private String buildActivePropPreviewCountText() {
        if (selectedRow == NONE || selectedCol == NONE) {
            return "";
        }
        int count = buildActivePropPreviewCells(selectedRow, selectedCol).size();
        return count > 1 ? " " + count + "格" : "";
    }

    private int getFirstRecommendedProp() {
        for (int prop = 0; prop < PROP_COUNT; prop++) {
            if (isRecommendedPropForLevel(prop)) {
                return prop;
            }
        }
        return NONE;
    }

    private String buildRecommendedPropTip(int prop) {
        Level level = levels.get(levelIndex);
        int obstaclePressure = iceRemaining + honeyRemaining + stoneRemaining + vineRemaining
                + chainRemaining + shellRemaining + coralReefRemaining + flowerRemaining;
        if (movesLeft <= Math.max(3, level.moves / 5)
                && (prop == PROP_EXTRA_MOVES || prop == PROP_CLOCK || prop == PROP_MOON_TICKET)) {
            return "推荐 " + getPropName(prop) + " 补步保星";
        } else if (!dailyChallengeMode && levelRanks[levelIndex] >= 4 && !levelPerfectCleared[levelIndex]
                && (prop == PROP_STAR_HARP || prop == PROP_FIREWORK_CANNON || prop == PROP_STAR_COMPASS)) {
            return "推荐 " + getPropName(prop) + " 冲完美";
        } else if (level.countdownBombCount > 0
                && (prop == PROP_SHIELD || prop == PROP_CLOCK || prop == PROP_SNOW_GLOBE)) {
            return "推荐 " + getPropName(prop) + " 稳住炸弹";
        } else if (isLastCountdownBombReady()
                && (prop == PROP_ROCKET || prop == PROP_LIGHTNING || prop == PROP_STAR_COMPASS || prop == PROP_HAMMER)) {
            return "推荐 " + getPropName(prop) + " 拆尾弹拿护盾";
        } else if (keyRemaining > 0
                && (prop == PROP_ROCKET || prop == PROP_LIGHTNING || prop == PROP_STAR_COMPASS || prop == PROP_HAMMER)) {
            return "推荐 " + getPropName(prop) + " 抢钥匙";
        } else if (isRewardCellMilestoneNear()
                && (prop == PROP_ROCKET || prop == PROP_LIGHTNING || prop == PROP_STAR_COMPASS || prop == PROP_HAMMER)) {
            return "推荐 " + getPropName(prop) + " 补1格拿罗盘";
        } else if (chainRemaining > 0 && prop == PROP_CHAIN_BREAKER) {
            return "推荐 " + getPropName(prop) + " 破锁开局";
        } else if (honeyRemaining > 0 && (prop == PROP_FREEZE || prop == PROP_SNOW_GLOBE)) {
            return "推荐 " + getPropName(prop) + " 压制蜂蜜";
        } else if (targetRemaining > level.targetAmount / 2 && movesLeft <= level.moves / 2
                && (prop == PROP_MAGNET || prop == PROP_COLOR_BLAST || prop == PROP_TARGET_BRUSH || prop == PROP_BRUSH)) {
            return "推荐 " + getPropName(prop) + " 补齐收集";
        } else if (getMusicBoxRemainingCount() > 0
                && (prop == PROP_ROCKET || prop == PROP_LIGHTNING || prop == PROP_STAR_COMPASS || prop == PROP_HAMMER)) {
            return "推荐 " + getPropName(prop) + " 开音乐盒拿星弦";
        } else if (getRewardCellCount() >= 3 && movesLeft <= level.moves / 2
                && (prop == PROP_ROCKET || prop == PROP_LIGHTNING || prop == PROP_STAR_COMPASS || prop == PROP_HAMMER)) {
            return "推荐 " + getPropName(prop) + " 收奖励格";
        } else if (obstaclePressure >= Math.max(5, getLevelObstacleCount(level) / 3)) {
            return "推荐 " + getPropName(prop) + " 清障打开局面";
        } else if (level.comboGoal > 0 && bestCombo < level.comboGoal) {
            return "推荐 " + getPropName(prop) + " 冲连击";
        } else if (level.scoreGoal > 0 && score < level.scoreGoal) {
            return "推荐 " + getPropName(prop) + " 冲高分";
        }
        return "推荐 " + getPropName(prop) + " 保留步数";
    }

    private boolean isRecommendedPropForLevel(int prop) {
        if (levelComplete) {
            return false;
        }
        if (propInventory[prop] <= 0 && coins < PROP_COSTS[prop]) {
            return false;
        }

        Level level = levels.get(levelIndex);
        int obstaclePressure = iceRemaining + honeyRemaining + stoneRemaining + vineRemaining
                + chainRemaining + shellRemaining + coralReefRemaining + flowerRemaining;
        if (movesLeft <= Math.max(3, level.moves / 5)
                && (prop == PROP_EXTRA_MOVES || prop == PROP_CLOCK || prop == PROP_MOON_TICKET)) {
            return true;
        }
        if (!dailyChallengeMode && levelRanks[levelIndex] >= 4 && !levelPerfectCleared[levelIndex]
                && (prop == PROP_STAR_HARP || prop == PROP_FIREWORK_CANNON || prop == PROP_STAR_COMPASS)) {
            return true;
        }
        if (isHiddenChallengeLevel() && movesUsed + 2 >= Math.max(7, level.moves - 4)
                && (prop == PROP_CLOCK || prop == PROP_EXTRA_MOVES || prop == PROP_MOON_TICKET)) {
            return true;
        }
        if (level.countdownBombCount > 0
                && (prop == PROP_SHIELD || prop == PROP_CLOCK || prop == PROP_SNOW_GLOBE)) {
            return true;
        }
        if (isLastCountdownBombReady()
                && (prop == PROP_ROCKET || prop == PROP_LIGHTNING || prop == PROP_STAR_COMPASS || prop == PROP_HAMMER)) {
            // 只剩最后一个炸弹时，推荐精准道具收尾并触发护盾奖励。
            return true;
        }
        if (keyRemaining > 0
                && (prop == PROP_ROCKET || prop == PROP_LIGHTNING || prop == PROP_STAR_COMPASS || prop == PROP_HAMMER)) {
            // 钥匙关优先推荐可直达关键格的道具，减少后期找不到落点的挫败感。
            return true;
        }
        if (isRewardCellMilestoneNear()
                && (prop == PROP_ROCKET || prop == PROP_LIGHTNING || prop == PROP_STAR_COMPASS || prop == PROP_HAMMER)) {
            // 奖励格只差1格触发罗盘时，优先推荐精准打点道具。
            return true;
        }
        if (chainRemaining > 0 && prop == PROP_CHAIN_BREAKER) {
            return true;
        }
        if (honeyRemaining > 0 && (prop == PROP_FREEZE || prop == PROP_SNOW_GLOBE)) {
            return true;
        }
        if (targetRemaining > level.targetAmount / 2 && movesLeft <= level.moves / 2
                && (prop == PROP_MAGNET || prop == PROP_COLOR_BLAST || prop == PROP_TARGET_BRUSH || prop == PROP_BRUSH)) {
            return true;
        }
        if (getMusicBoxRemainingCount() > 0
                && (prop == PROP_ROCKET || prop == PROP_LIGHTNING || prop == PROP_STAR_COMPASS || prop == PROP_HAMMER)) {
            // 音乐盒能转成可储备星弦琴，推荐精准道具优先开盒。
            return true;
        }
        if (getRewardCellCount() >= 3 && movesLeft <= level.moves / 2
                && (prop == PROP_ROCKET || prop == PROP_LIGHTNING || prop == PROP_STAR_COMPASS || prop == PROP_HAMMER)) {
            // 奖励格密集且步数吃紧时，推荐能精准打到关键格的道具。
            return true;
        }
        if (obstaclePressure >= Math.max(5, getLevelObstacleCount(level) / 3) && movesLeft <= level.moves * 2 / 3
                && (prop == PROP_CLEANSE || prop == PROP_STARFISH_PICK || prop == PROP_BUBBLE_WAND
                || prop == PROP_METEOR || prop == PROP_LIGHTNING)) {
            return true;
        }
        if (level.comboGoal > 0 && bestCombo < level.comboGoal
                && (prop == PROP_MAGIC_WAND || prop == PROP_BOMB || prop == PROP_ROCKET || prop == PROP_STAR_HARP)) {
            return true;
        }
        if (level.scoreGoal > 0 && score < level.scoreGoal && movesLeft <= level.moves / 2
                && (prop == PROP_BOMB || prop == PROP_ROCKET || prop == PROP_FIREWORK_CANNON || prop == PROP_STAR_COMPASS)) {
            return true;
        }
        return level.moveLimitGoal > 0 && movesUsed + 2 >= getMoveLimitGoal(level)
                && (prop == PROP_EXTRA_MOVES || prop == PROP_CLOCK || prop == PROP_MOON_TICKET);
    }

    private boolean isRewardCellMilestoneNear() {
        return rewardCellClearedCount % 3 == 2 && getRewardCellCount() > 0;
    }

    private boolean isLastCountdownBombReady() {
        return levels.get(levelIndex).countdownBombCount > 0 && rewardBombMilestone == 0
                && getCountdownBombRemainingCount() == 1;
    }

    private String buildFailurePropAdviceText() {
        if (getMusicBoxRemainingCount() > 0) {
            // 音乐盒关失败时直接提示资源目标，帮助下局优先规划星弦琴储备。
            return "建议下局优先开音乐盒";
        }
        for (int prop = 0; prop < PROP_COUNT; prop++) {
            if (isRecommendedPropForLevel(prop)) {
                // 失败页复用局内推荐逻辑，提示下局优先使用的道具。
                return "建议下局优先 " + getPropName(prop);
            }
        }
        return "";
    }

    private void drawRecommendedPropGlow(Canvas canvas, RectF rect) {
        // 推荐框只做视觉提示，不自动消耗道具，避免打断玩家的主动选择。
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.argb(220, 255, 236, 133));
        canvas.drawRoundRect(new RectF(rect.left + dp(2), rect.top + dp(2),
                rect.right - dp(2), rect.bottom - dp(2)), dp(10), dp(10), paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawRecommendedPropBadge(Canvas canvas, RectF rect) {
        RectF badge = new RectF(rect.right - dp(20), rect.top + dp(4), rect.right - dp(4), rect.top + dp(18));
        paint.setColor(Color.argb(225, 255, 236, 133));
        canvas.drawRoundRect(badge, dp(5), dp(5), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(8));
        textPaint.setColor(Color.rgb(33, 37, 56));
        canvas.drawText("荐", badge.centerX(), badge.centerY() + dp(3), textPaint);
    }

    private void drawReservePropBadge(Canvas canvas, RectF rect, int amount) {
        // 储备角标单独显示，避免道具名、库存和储备数量互相挤压。
        RectF badge = new RectF(rect.left + dp(4), rect.top + dp(4), rect.left + dp(28), rect.top + dp(18));
        paint.setColor(Color.argb(225, 116, 219, 214));
        canvas.drawRoundRect(badge, dp(5), dp(5), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(8));
        textPaint.setColor(Color.rgb(33, 37, 56));
        canvas.drawText("储" + amount, badge.centerX(), badge.centerY() + dp(3), textPaint);
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
        } else if (prop == PROP_MAGIC_WAND) {
            paint.setStrokeWidth(dp(4));
            canvas.drawLine(centerX - dp(12), centerY + dp(12), centerX + dp(10), centerY - dp(10), paint);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX + dp(12), centerY - dp(12), dp(5), paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centerX - dp(14), centerY + dp(14), dp(3), paint);
        } else if (prop == PROP_PORTAL) {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, dp(14), paint);
            canvas.drawArc(new RectF(centerX - dp(14), centerY - dp(14), centerX + dp(14), centerY + dp(14)),
                    40, 250, false, paint);
            paint.setStyle(Paint.Style.FILL);
        } else if (prop == PROP_CLEANSE) {
            canvas.drawCircle(centerX, centerY, dp(14), paint);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(dp(3));
            canvas.drawLine(centerX - dp(8), centerY, centerX - dp(1), centerY + dp(7), paint);
            canvas.drawLine(centerX - dp(1), centerY + dp(7), centerX + dp(10), centerY - dp(8), paint);
        } else if (prop == PROP_FREEZE) {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, dp(13), paint);
            canvas.drawLine(centerX - dp(10), centerY, centerX + dp(10), centerY, paint);
            canvas.drawLine(centerX, centerY - dp(10), centerX, centerY + dp(10), paint);
            canvas.drawLine(centerX - dp(7), centerY - dp(7), centerX + dp(7), centerY + dp(7), paint);
            canvas.drawLine(centerX + dp(7), centerY - dp(7), centerX - dp(7), centerY + dp(7), paint);
            paint.setStyle(Paint.Style.FILL);
        } else if (prop == PROP_MAGNET) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(4));
            canvas.drawArc(new RectF(centerX - dp(13), centerY - dp(13),
                    centerX + dp(13), centerY + dp(13)), 35, 250, false, paint);
            canvas.drawLine(centerX - dp(10), centerY + dp(8), centerX - dp(4), centerY + dp(15), paint);
            canvas.drawLine(centerX + dp(10), centerY + dp(8), centerX + dp(4), centerY + dp(15), paint);
            paint.setStyle(Paint.Style.FILL);
        } else if (prop == PROP_CLOCK) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(3));
            canvas.drawCircle(centerX, centerY, dp(14), paint);
            canvas.drawLine(centerX, centerY, centerX, centerY - dp(8), paint);
            canvas.drawLine(centerX, centerY, centerX + dp(7), centerY + dp(5), paint);
            paint.setStyle(Paint.Style.FILL);
        } else if (prop == PROP_STAR_HAMMER) {
            canvas.drawRoundRect(new RectF(centerX - dp(11), centerY - dp(12),
                    centerX + dp(12), centerY - dp(4)), dp(4), dp(4), paint);
            canvas.drawLine(centerX + dp(5), centerY, centerX + dp(16), centerY + dp(14), paint);
            drawPropStar(canvas, centerX - dp(13), centerY + dp(11), dp(7));
        } else if (prop == PROP_ROCKET) {
            Path rocket = new Path();
            rocket.moveTo(centerX, centerY - dp(17));
            rocket.lineTo(centerX + dp(12), centerY + dp(10));
            rocket.lineTo(centerX, centerY + dp(4));
            rocket.lineTo(centerX - dp(12), centerY + dp(10));
            rocket.close();
            canvas.drawPath(rocket, paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(centerX, centerY - dp(3), dp(4), paint);
        } else if (prop == PROP_TARGET_BRUSH) {
            paint.setStrokeWidth(dp(4));
            canvas.drawLine(centerX - dp(12), centerY + dp(12), centerX + dp(9), centerY - dp(9), paint);
            paint.setColor(palette[targetKind]);
            canvas.drawCircle(centerX + dp(12), centerY - dp(12), dp(7), paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(centerX + dp(10), centerY - dp(14), dp(2), paint);
        } else if (prop == PROP_SHIELD) {
            Path shield = new Path();
            shield.moveTo(centerX, centerY - dp(16));
            shield.lineTo(centerX + dp(14), centerY - dp(8));
            shield.quadTo(centerX + dp(10), centerY + dp(12), centerX, centerY + dp(17));
            shield.quadTo(centerX - dp(10), centerY + dp(12), centerX - dp(14), centerY - dp(8));
            shield.close();
            canvas.drawPath(shield, paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(centerX, centerY, dp(5), paint);
        } else if (prop == PROP_ENERGY_CORE) {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(centerX, centerY, dp(14), paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            drawPropStar(canvas, centerX, centerY, dp(8));
            paint.setColor(Color.argb(220, 255, 236, 118));
            canvas.drawCircle(centerX, centerY, dp(4), paint);
        } else if (prop == PROP_CHAIN_BREAKER) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(4));
            canvas.drawArc(new RectF(centerX - dp(15), centerY - dp(12),
                    centerX + dp(4), centerY + dp(12)), 290, 220, false, paint);
            canvas.drawArc(new RectF(centerX - dp(4), centerY - dp(12),
                    centerX + dp(15), centerY + dp(12)), 70, 220, false, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawLine(centerX - dp(8), centerY + dp(11), centerX + dp(8), centerY - dp(11), paint);
        } else if (prop == PROP_LIGHTNING) {
            Path bolt = new Path();
            bolt.moveTo(centerX + dp(4), centerY - dp(17));
            bolt.lineTo(centerX - dp(9), centerY + dp(1));
            bolt.lineTo(centerX + dp(1), centerY + dp(1));
            bolt.lineTo(centerX - dp(4), centerY + dp(17));
            bolt.lineTo(centerX + dp(12), centerY - dp(4));
            bolt.lineTo(centerX + dp(2), centerY - dp(4));
            bolt.close();
            canvas.drawPath(bolt, paint);
        } else if (prop == PROP_METEOR) {
            drawPropStar(canvas, centerX - dp(5), centerY - dp(5), dp(10));
            paint.setStrokeWidth(dp(4));
            canvas.drawLine(centerX + dp(12), centerY - dp(16), centerX - dp(12), centerY + dp(16), paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(centerX - dp(7), centerY + dp(8), dp(4), paint);
        } else if (prop == PROP_TIDE) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(4));
            for (int i = 0; i < 3; i++) {
                float y = centerY - dp(10) + i * dp(9);
                canvas.drawArc(new RectF(centerX - dp(16), y - dp(7),
                        centerX + dp(16), y + dp(7)), 200, 140, false, paint);
            }
            paint.setStyle(Paint.Style.FILL);
        } else if (prop == PROP_AURORA_ORB) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(3));
            canvas.drawCircle(centerX, centerY, dp(14), paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(palette[(movesUsed + 2) % TILE_KINDS]);
            canvas.drawCircle(centerX, centerY, dp(9), paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(centerX - dp(4), centerY - dp(4), dp(3), paint);
        } else if (prop == PROP_STARFISH_PICK) {
            drawPropStar(canvas, centerX - dp(6), centerY - dp(4), dp(11));
            paint.setStrokeWidth(dp(4));
            canvas.drawLine(centerX + dp(3), centerY + dp(2), centerX + dp(15), centerY + dp(15), paint);
            paint.setColor(Color.WHITE);
            canvas.drawLine(centerX + dp(8), centerY + dp(7), centerX + dp(2), centerY + dp(13), paint);
        } else if (prop == PROP_MOON_TICKET) {
            RectF ticket = new RectF(centerX - dp(15), centerY - dp(10), centerX + dp(15), centerY + dp(10));
            canvas.drawRoundRect(ticket, dp(4), dp(4), paint);
            paint.setColor(Color.WHITE);
            drawPropStar(canvas, centerX - dp(5), centerY, dp(5));
            canvas.drawCircle(centerX + dp(8), centerY, dp(4), paint);
        } else if (prop == PROP_FIREWORK_CANNON) {
            RectF barrel = new RectF(centerX - dp(13), centerY - dp(7), centerX + dp(12), centerY + dp(9));
            canvas.drawRoundRect(barrel, dp(5), dp(5), paint);
            paint.setColor(Color.WHITE);
            canvas.drawLine(centerX + dp(8), centerY - dp(7), centerX + dp(15), centerY - dp(15), paint);
            drawPropStar(canvas, centerX + dp(16), centerY - dp(17), dp(6));
        } else if (prop == PROP_STAR_COMPASS) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(3));
            canvas.drawCircle(centerX, centerY, dp(14), paint);
            canvas.drawLine(centerX - dp(15), centerY, centerX + dp(15), centerY, paint);
            canvas.drawLine(centerX, centerY - dp(15), centerX, centerY + dp(15), paint);
            canvas.drawLine(centerX - dp(10), centerY - dp(10), centerX + dp(10), centerY + dp(10), paint);
            canvas.drawLine(centerX + dp(10), centerY - dp(10), centerX - dp(10), centerY + dp(10), paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            drawPropStar(canvas, centerX, centerY, dp(5));
        } else if (prop == PROP_BUBBLE_WAND) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(3));
            canvas.drawLine(centerX - dp(13), centerY + dp(14), centerX + dp(6), centerY - dp(5), paint);
            canvas.drawCircle(centerX + dp(10), centerY - dp(10), dp(8), paint);
            canvas.drawCircle(centerX - dp(6), centerY - dp(4), dp(5), paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(centerX + dp(7), centerY - dp(13), dp(2), paint);
        } else if (prop == PROP_SNOW_GLOBE) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(3));
            canvas.drawCircle(centerX, centerY - dp(2), dp(14), paint);
            canvas.drawLine(centerX - dp(13), centerY + dp(14), centerX + dp(13), centerY + dp(14), paint);
            canvas.drawLine(centerX - dp(7), centerY + dp(20), centerX + dp(7), centerY + dp(20), paint);
            canvas.drawLine(centerX - dp(9), centerY - dp(2), centerX + dp(9), centerY - dp(2), paint);
            canvas.drawLine(centerX, centerY - dp(11), centerX, centerY + dp(7), paint);
            canvas.drawLine(centerX - dp(6), centerY - dp(8), centerX + dp(6), centerY + dp(4), paint);
            canvas.drawLine(centerX + dp(6), centerY - dp(8), centerX - dp(6), centerY + dp(4), paint);
            paint.setStyle(Paint.Style.FILL);
        } else if (prop == PROP_STAR_HARP) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(3));
            RectF body = new RectF(centerX - dp(13), centerY - dp(17), centerX + dp(13), centerY + dp(17));
            canvas.drawArc(body, 250, 220, false, paint);
            canvas.drawLine(centerX - dp(9), centerY - dp(13), centerX - dp(9), centerY + dp(13), paint);
            canvas.drawLine(centerX - dp(2), centerY - dp(10), centerX - dp(2), centerY + dp(10), paint);
            canvas.drawLine(centerX + dp(5), centerY - dp(6), centerX + dp(5), centerY + dp(8), paint);
            paint.setStyle(Paint.Style.FILL);
            drawPropStar(canvas, centerX + dp(12), centerY - dp(14), dp(6));
        } else {
            canvas.drawRoundRect(new RectF(centerX - dp(13), centerY - dp(10), centerX + dp(13), centerY - dp(2)),
                    dp(4), dp(4), paint);
            canvas.drawLine(centerX - dp(8), centerY + dp(3), centerX + dp(8), centerY + dp(15), paint);
        }
    }

    private void drawPropStar(Canvas canvas, float centerX, float centerY, float radius) {
        Path star = new Path();
        for (int i = 0; i < 10; i++) {
            double angle = -Math.PI / 2 + i * Math.PI / 5;
            float pointRadius = i % 2 == 0 ? radius : radius * 0.45f;
            float x = centerX + (float) Math.cos(angle) * pointRadius;
            float y = centerY + (float) Math.sin(angle) * pointRadius;
            if (i == 0) {
                star.moveTo(x, y);
            } else {
                star.lineTo(x, y);
            }
        }
        star.close();
        canvas.drawPath(star, paint);
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
        } else if (prop == PROP_BRUSH) {
            return "克隆";
        } else if (prop == PROP_PORTAL) {
            return "传送";
        } else if (prop == PROP_CLEANSE) {
            return "净化";
        } else if (prop == PROP_FREEZE) {
            return "冻结";
        } else if (prop == PROP_MAGNET) {
            return "磁铁";
        } else if (prop == PROP_CLOCK) {
            return "时钟";
        } else if (prop == PROP_STAR_HAMMER) {
            return "星锤";
        } else if (prop == PROP_ROCKET) {
            return "火箭";
        } else if (prop == PROP_TARGET_BRUSH) {
            return "目标刷";
        } else if (prop == PROP_SHIELD) {
            return "护盾";
        } else if (prop == PROP_ENERGY_CORE) {
            return "核心";
        } else if (prop == PROP_CHAIN_BREAKER) {
            return "破锁";
        } else if (prop == PROP_LIGHTNING) {
            return "闪电";
        } else if (prop == PROP_METEOR) {
            return "流星";
        } else if (prop == PROP_TIDE) {
            return "潮汐";
        } else if (prop == PROP_AURORA_ORB) {
            return "极光";
        } else if (prop == PROP_STARFISH_PICK) {
            return "海星镐";
        } else if (prop == PROP_MOON_TICKET) {
            return "月票";
        } else if (prop == PROP_FIREWORK_CANNON) {
            return "礼炮";
        } else if (prop == PROP_STAR_COMPASS) {
            return "罗盘";
        } else if (prop == PROP_BUBBLE_WAND) {
            return "泡泡棒";
        } else if (prop == PROP_SNOW_GLOBE) {
            return "雪花球";
        } else if (prop == PROP_STAR_HARP) {
            return "星弦琴";
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

    private void drawCoralReef(Canvas canvas, int row, int col, RectF rect) {
        if (coralReef[row][col] <= 0) {
            return;
        }

        float centerX = rect.centerX();
        float baseY = rect.bottom - dp(9);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(4));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(coralReef[row][col] > 1 ? Color.argb(210, 255, 126, 133) : Color.argb(175, 255, 184, 96));
        for (int i = -2; i <= 2; i++) {
            float startX = centerX + i * dp(6);
            float height = dp(16 + (i % 2 == 0 ? 4 : 0));
            canvas.drawLine(startX, baseY, startX + i * dp(2), baseY - height, paint);
            canvas.drawLine(startX + i * dp(2), baseY - height + dp(4),
                    startX + i * dp(2) + (i <= 0 ? -dp(5) : dp(5)), baseY - height - dp(2), paint);
        }
        if (coralReef[row][col] == 1) {
            paint.setStrokeWidth(dp(2));
            paint.setColor(Color.argb(220, 120, 86, 118));
            canvas.drawLine(rect.left + dp(13), rect.centerY(), rect.right - dp(12), rect.centerY() + dp(7), paint);
        }
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawFlower(Canvas canvas, int row, int col, RectF rect) {
        if (flower[row][col] <= 0) {
            return;
        }

        float centerX = rect.centerX();
        float centerY = rect.centerY();
        paint.setColor(flower[row][col] > 1 ? Color.argb(185, 255, 139, 176) : Color.argb(165, 255, 186, 82));
        for (int i = 0; i < 5; i++) {
            double angle = -Math.PI / 2 + i * Math.PI * 2 / 5;
            float petalX = centerX + (float) Math.cos(angle) * dp(12);
            float petalY = centerY + (float) Math.sin(angle) * dp(12);
            canvas.drawOval(new RectF(petalX - dp(6), petalY - dp(8), petalX + dp(6), petalY + dp(8)), paint);
        }
        paint.setColor(Color.argb(220, 255, 236, 118));
        canvas.drawCircle(centerX, centerY, flower[row][col] > 1 ? dp(7) : dp(9), paint);
        if (flower[row][col] == 1) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(Color.argb(215, 120, 92, 120));
            canvas.drawLine(centerX - dp(14), centerY, centerX + dp(12), centerY - dp(7), paint);
            canvas.drawLine(centerX - dp(8), centerY - dp(12), centerX + dp(9), centerY + dp(12), paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    private void drawCloud(Canvas canvas, int row, int col, RectF rect) {
        if (cloud[row][col] <= 0) {
            return;
        }

        paint.setColor(Color.argb(125, 255, 255, 255));
        float drift = (float) Math.sin(System.currentTimeMillis() / 260.0 + row + col) * dp(2);
        canvas.drawCircle(rect.left + rect.width() * 0.32f + drift, rect.bottom - dp(12), dp(7), paint);
        canvas.drawCircle(rect.left + rect.width() * 0.48f + drift, rect.bottom - dp(16), dp(8), paint);
        canvas.drawCircle(rect.left + rect.width() * 0.65f + drift, rect.bottom - dp(12), dp(7), paint);
        postInvalidateOnAnimation();
    }

    private void drawGem(Canvas canvas, int row, int col, RectF rect) {
        if (gem[row][col] <= 0) {
            return;
        }

        Path path = new Path();
        float centerX = rect.left + rect.width() * 0.22f;
        float centerY = rect.bottom - dp(16);
        path.moveTo(centerX, centerY - dp(11));
        path.lineTo(centerX + dp(10), centerY - dp(2));
        path.lineTo(centerX + dp(6), centerY + dp(11));
        path.lineTo(centerX - dp(6), centerY + dp(11));
        path.lineTo(centerX - dp(10), centerY - dp(2));
        path.close();
        paint.setColor(Color.argb(220, 116, 219, 214));
        canvas.drawPath(path, paint);
        paint.setColor(Color.argb(210, 255, 255, 255));
        canvas.drawCircle(centerX - dp(3), centerY - dp(3), dp(3), paint);
    }

    private void drawGoldenEgg(Canvas canvas, int row, int col, RectF rect) {
        if (goldenEgg[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.top + dp(19);
        paint.setColor(Color.argb(225, 255, 196, 64));
        canvas.drawOval(new RectF(centerX - dp(10), centerY - dp(13), centerX + dp(10), centerY + dp(13)), paint);
        paint.setColor(Color.argb(180, 255, 255, 255));
        canvas.drawCircle(centerX - dp(3), centerY - dp(5), dp(3), paint);
    }

    private void drawCoinPouch(Canvas canvas, int row, int col, RectF rect) {
        if (coinPouch[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.bottom - dp(18);
        paint.setColor(Color.argb(225, 255, 186, 82));
        Path pouch = new Path();
        pouch.moveTo(centerX - dp(11), centerY - dp(3));
        pouch.quadTo(centerX, centerY - dp(16), centerX + dp(11), centerY - dp(3));
        pouch.quadTo(centerX + dp(13), centerY + dp(13), centerX, centerY + dp(13));
        pouch.quadTo(centerX - dp(13), centerY + dp(13), centerX - dp(11), centerY - dp(3));
        pouch.close();
        canvas.drawPath(pouch, paint);
        paint.setColor(Color.argb(230, 33, 37, 56));
        paint.setStrokeWidth(dp(2));
        canvas.drawLine(centerX - dp(8), centerY - dp(4), centerX + dp(8), centerY - dp(4), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(10));
        textPaint.setColor(Color.WHITE);
        canvas.drawText("$", centerX, centerY + dp(8), textPaint);
    }

    private void drawPaintBucket(Canvas canvas, int row, int col, RectF rect) {
        if (paintBucket[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.25f;
        float centerY = rect.top + dp(19);
        paint.setColor(Color.argb(220, 255, 255, 255));
        RectF bucket = new RectF(centerX - dp(10), centerY - dp(8), centerX + dp(10), centerY + dp(10));
        canvas.drawRoundRect(bucket, dp(4), dp(4), paint);
        paint.setColor(palette[targetKind]);
        canvas.drawRect(bucket.left + dp(3), bucket.centerY(), bucket.right - dp(3), bucket.bottom - dp(2), paint);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.argb(210, 33, 37, 56));
        canvas.drawLine(bucket.left + dp(2), bucket.top, bucket.right - dp(2), bucket.top, paint);
        paint.setColor(palette[targetKind]);
        canvas.drawCircle(centerX + dp(12), centerY + dp(12), dp(4), paint);
    }

    private void drawWindmill(Canvas canvas, int row, int col, RectF rect) {
        if (windmill[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.top + dp(19);
        paint.setColor(Color.argb(225, 255, 255, 255));
        for (int i = 0; i < 4; i++) {
            canvas.save();
            canvas.rotate(i * 90 + (System.currentTimeMillis() / 20) % 90, centerX, centerY);
            Path blade = new Path();
            blade.moveTo(centerX, centerY);
            blade.lineTo(centerX + dp(3), centerY - dp(15));
            blade.lineTo(centerX + dp(13), centerY - dp(5));
            blade.close();
            canvas.drawPath(blade, paint);
            canvas.restore();
        }
        paint.setColor(Color.argb(230, 255, 159, 64));
        canvas.drawCircle(centerX, centerY, dp(4), paint);
        postInvalidateOnAnimation();
    }

    private void drawJewelBow(Canvas canvas, int row, int col, RectF rect) {
        if (jewelBow[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.24f;
        float centerY = rect.top + dp(20);
        paint.setColor(Color.argb(220, 255, 139, 176));
        canvas.drawOval(new RectF(centerX - dp(15), centerY - dp(8), centerX - dp(2), centerY + dp(8)), paint);
        canvas.drawOval(new RectF(centerX + dp(2), centerY - dp(8), centerX + dp(15), centerY + dp(8)), paint);
        paint.setColor(palette[targetKind]);
        canvas.drawCircle(centerX, centerY, dp(6), paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerX - dp(2), centerY - dp(2), dp(2), paint);
    }

    private void drawStardustJar(Canvas canvas, int row, int col, RectF rect) {
        if (stardustJar[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.bottom - dp(18);
        paint.setColor(Color.argb(220, 255, 255, 255));
        canvas.drawRoundRect(new RectF(centerX - dp(9), centerY - dp(14),
                centerX + dp(9), centerY + dp(12)), dp(5), dp(5), paint);
        paint.setColor(Color.argb(220, 153, 102, 255));
        canvas.drawRoundRect(new RectF(centerX - dp(7), centerY - dp(4),
                centerX + dp(7), centerY + dp(10)), dp(4), dp(4), paint);
        paint.setColor(Color.argb(230, 255, 236, 118));
        drawStar(canvas, centerX, centerY - dp(2), dp(6));
    }

    private void drawWishLamp(Canvas canvas, int row, int col, RectF rect) {
        if (wishLamp[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.24f;
        float centerY = rect.bottom - dp(18);
        paint.setColor(Color.argb(225, 255, 196, 82));
        canvas.drawOval(new RectF(centerX - dp(13), centerY - dp(8), centerX + dp(13), centerY + dp(9)), paint);
        paint.setColor(Color.argb(230, 255, 236, 118));
        canvas.drawRoundRect(new RectF(centerX - dp(8), centerY - dp(18),
                centerX + dp(8), centerY - dp(2)), dp(8), dp(8), paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerX - dp(3), centerY - dp(10), dp(3), paint);
    }

    private void drawResonanceDrum(Canvas canvas, int row, int col, RectF rect) {
        if (resonanceDrum[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.top + dp(19);
        paint.setColor(Color.argb(225, 255, 159, 64));
        canvas.drawOval(new RectF(centerX - dp(13), centerY - dp(8), centerX + dp(13), centerY + dp(8)), paint);
        paint.setColor(Color.argb(230, 255, 236, 118));
        canvas.drawRoundRect(new RectF(centerX - dp(13), centerY - dp(4),
                centerX + dp(13), centerY + dp(12)), dp(5), dp(5), paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerX, centerY - dp(2), dp(5), paint);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.argb(210, 33, 37, 56));
        canvas.drawLine(centerX - dp(16), centerY - dp(12), centerX - dp(6), centerY - dp(5), paint);
        canvas.drawLine(centerX + dp(16), centerY - dp(12), centerX + dp(6), centerY - dp(5), paint);
    }

    private void drawAuroraPrism(Canvas canvas, int row, int col, RectF rect) {
        if (auroraPrism[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.26f;
        float centerY = rect.top + dp(20);
        Path prism = new Path();
        prism.moveTo(centerX, centerY - dp(14));
        prism.lineTo(centerX + dp(13), centerY + dp(10));
        prism.lineTo(centerX - dp(13), centerY + dp(10));
        prism.close();
        paint.setColor(Color.argb(220, 255, 255, 255));
        canvas.drawPath(prism, paint);
        paint.setColor(palette[(row + col + movesUsed) % TILE_KINDS]);
        canvas.drawCircle(centerX, centerY + dp(3), dp(6), paint);
        paint.setColor(Color.argb(210, 116, 219, 214));
        canvas.drawLine(centerX + dp(11), centerY + dp(4), centerX + dp(20), centerY - dp(3), paint);
    }

    private void drawRainbowBottle(Canvas canvas, int row, int col, RectF rect) {
        if (rainbowBottle[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.24f;
        float centerY = rect.top + dp(20);
        paint.setColor(Color.argb(220, 255, 255, 255));
        canvas.drawRoundRect(new RectF(centerX - dp(7), centerY - dp(13),
                centerX + dp(7), centerY + dp(12)), dp(5), dp(5), paint);
        paint.setColor(palette[(row + col + movesUsed) % TILE_KINDS]);
        canvas.drawRoundRect(new RectF(centerX - dp(5), centerY - dp(4),
                centerX + dp(5), centerY + dp(10)), dp(4), dp(4), paint);
    }

    private void drawEnergyPotion(Canvas canvas, int row, int col, RectF rect) {
        if (energyPotion[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.bottom - dp(18);
        paint.setColor(Color.argb(220, 116, 219, 214));
        canvas.drawCircle(centerX, centerY, dp(12), paint);
        paint.setColor(Color.argb(220, 255, 236, 118));
        canvas.drawCircle(centerX, centerY, dp(5), paint);
    }

    private void drawButterfly(Canvas canvas, int row, int col, RectF rect) {
        if (butterfly[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.25f;
        float centerY = rect.bottom - dp(18);
        paint.setColor(Color.argb(210, 255, 139, 176));
        canvas.drawOval(new RectF(centerX - dp(14), centerY - dp(9), centerX - dp(2), centerY + dp(9)), paint);
        paint.setColor(Color.argb(210, 116, 219, 214));
        canvas.drawOval(new RectF(centerX + dp(2), centerY - dp(9), centerX + dp(14), centerY + dp(9)), paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerX, centerY, dp(3), paint);
    }

    private void drawPortal(Canvas canvas, int row, int col, RectF rect) {
        if (portal[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.bottom - dp(18);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        paint.setColor(Color.argb(220, 153, 102, 255));
        canvas.drawCircle(centerX, centerY, dp(12), paint);
        paint.setColor(Color.argb(190, 255, 236, 118));
        canvas.drawArc(new RectF(centerX - dp(12), centerY - dp(12), centerX + dp(12), centerY + dp(12)),
                (System.currentTimeMillis() / 8) % 360, 230, false, paint);
        paint.setStyle(Paint.Style.FILL);
        postInvalidateOnAnimation();
    }

    private void drawHourglass(Canvas canvas, int row, int col, RectF rect) {
        if (hourglass[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.top + dp(18);
        Path glass = new Path();
        glass.moveTo(centerX - dp(11), centerY - dp(12));
        glass.lineTo(centerX + dp(11), centerY - dp(12));
        glass.lineTo(centerX - dp(8), centerY + dp(12));
        glass.lineTo(centerX + dp(8), centerY + dp(12));
        paint.setColor(Color.argb(210, 255, 236, 133));
        canvas.drawPath(glass, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerX, centerY, dp(4), paint);
    }

    private void drawLuckyStar(Canvas canvas, int row, int col, RectF rect) {
        if (luckyStar[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.24f;
        float centerY = rect.top + dp(18);
        Path star = new Path();
        for (int i = 0; i < 10; i++) {
            double angle = -Math.PI / 2 + i * Math.PI / 5;
            float radius = i % 2 == 0 ? dp(12) : dp(5);
            float x = centerX + (float) Math.cos(angle) * radius;
            float y = centerY + (float) Math.sin(angle) * radius;
            if (i == 0) {
                star.moveTo(x, y);
            } else {
                star.lineTo(x, y);
            }
        }
        star.close();
        paint.setColor(Color.argb(225, 255, 236, 118));
        canvas.drawPath(star, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerX - dp(3), centerY - dp(3), dp(3), paint);
    }

    private void drawLuckyClover(Canvas canvas, int row, int col, RectF rect) {
        if (luckyClover[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.28f;
        float centerY = rect.bottom - dp(18);
        paint.setColor(Color.argb(225, 80, 198, 126));
        canvas.drawCircle(centerX - dp(6), centerY - dp(5), dp(7), paint);
        canvas.drawCircle(centerX + dp(6), centerY - dp(5), dp(7), paint);
        canvas.drawCircle(centerX - dp(5), centerY + dp(6), dp(7), paint);
        canvas.drawCircle(centerX + dp(5), centerY + dp(6), dp(7), paint);
        paint.setStrokeWidth(dp(3));
        paint.setColor(Color.argb(230, 48, 126, 83));
        canvas.drawLine(centerX, centerY + dp(7), centerX + dp(9), centerY + dp(16), paint);
        paint.setColor(Color.argb(225, 255, 255, 255));
        canvas.drawCircle(centerX - dp(3), centerY - dp(3), dp(3), paint);
    }

    private void drawMysteryBox(Canvas canvas, int row, int col, RectF rect) {
        if (mysteryBox[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.bottom - dp(18);
        paint.setColor(Color.argb(220, 255, 159, 64));
        RectF box = new RectF(centerX - dp(12), centerY - dp(12), centerX + dp(12), centerY + dp(12));
        canvas.drawRoundRect(box, dp(6), dp(6), paint);
        paint.setColor(Color.argb(230, 153, 102, 255));
        canvas.drawRect(box.left + dp(9), box.top, box.right - dp(9), box.bottom, paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(16));
        textPaint.setColor(Color.WHITE);
        canvas.drawText("?", centerX, centerY + dp(6), textPaint);
    }

    private void drawPearl(Canvas canvas, int row, int col, RectF rect) {
        if (pearl[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.top + dp(18);
        paint.setColor(Color.argb(185, 255, 245, 220));
        canvas.drawOval(new RectF(centerX - dp(13), centerY - dp(9), centerX + dp(13), centerY + dp(10)), paint);
        paint.setColor(Color.argb(235, 255, 255, 255));
        canvas.drawCircle(centerX, centerY, dp(8), paint);
        paint.setColor(Color.argb(190, 255, 184, 220));
        canvas.drawCircle(centerX - dp(3), centerY - dp(3), dp(3), paint);
    }

    private void drawCarousel(Canvas canvas, int row, int col, RectF rect) {
        if (carousel[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.26f;
        float centerY = rect.top + dp(18);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.argb(220, 255, 236, 118));
        canvas.drawCircle(centerX, centerY, dp(13), paint);
        for (int i = 0; i < 4; i++) {
            double angle = System.currentTimeMillis() / 500.0 + i * Math.PI / 2;
            canvas.drawLine(centerX, centerY, centerX + (float) Math.cos(angle) * dp(12),
                    centerY + (float) Math.sin(angle) * dp(12), paint);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(230, 255, 174, 208));
        canvas.drawCircle(centerX, centerY, dp(4), paint);
        postInvalidateOnAnimation();
    }

    private void drawFerrisTicket(Canvas canvas, int row, int col, RectF rect) {
        if (ferrisTicket[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.bottom - dp(18);
        RectF ticket = new RectF(centerX - dp(14), centerY - dp(9), centerX + dp(14), centerY + dp(9));
        paint.setColor(Color.argb(230, 255, 236, 118));
        canvas.drawRoundRect(ticket, dp(4), dp(4), paint);
        paint.setColor(Color.argb(210, 110, 125, 255));
        canvas.drawCircle(ticket.left, centerY, dp(4), paint);
        canvas.drawCircle(ticket.right, centerY, dp(4), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(10));
        textPaint.setColor(Color.rgb(33, 37, 56));
        canvas.drawText("月", centerX, centerY + dp(4), textPaint);
        textPaint.setColor(Color.WHITE);
    }

    private void drawFireworksBarrel(Canvas canvas, int row, int col, RectF rect) {
        if (fireworksBarrel[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.26f;
        float centerY = rect.bottom - dp(18);
        RectF body = new RectF(centerX - dp(10), centerY - dp(10), centerX + dp(10), centerY + dp(12));
        paint.setColor(Color.argb(230, 255, 99, 132));
        canvas.drawRoundRect(body, dp(5), dp(5), paint);
        paint.setColor(Color.argb(230, 255, 236, 118));
        canvas.drawRect(body.left + dp(4), body.top, body.right - dp(4), body.bottom, paint);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.WHITE);
        canvas.drawLine(centerX, body.top, centerX + dp(7), body.top - dp(8), paint);
        drawPropStar(canvas, centerX + dp(10), body.top - dp(10), dp(5));
    }

    private void drawStarportBeacon(Canvas canvas, int row, int col, RectF rect) {
        if (starportBeacon[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.top + dp(18);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.argb(230, 255, 236, 118));
        canvas.drawCircle(centerX, centerY, dp(13), paint);
        canvas.drawLine(centerX, centerY + dp(13), centerX, centerY + dp(21), paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        drawPropStar(canvas, centerX, centerY, dp(7));
    }

    private void drawMeteorTrail(Canvas canvas, int row, int col, RectF rect) {
        if (meteorTrail[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.30f;
        float centerY = rect.top + rect.height() * 0.30f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        paint.setColor(Color.argb(225, 116, 219, 214));
        canvas.drawLine(rect.left + dp(10), rect.top + dp(12), rect.right - dp(10), rect.bottom - dp(12), paint);
        paint.setColor(Color.argb(190, 255, 236, 118));
        canvas.drawLine(rect.right - dp(12), rect.top + dp(12), rect.left + dp(12), rect.bottom - dp(12), paint);
        paint.setStyle(Paint.Style.FILL);
        drawPropStar(canvas, centerX, centerY, dp(6));
    }

    private void drawRainbowArc(Canvas canvas, int row, int col, RectF rect) {
        if (rainbowArc[row][col] <= 0) {
            return;
        }

        float centerX = rect.centerX();
        float centerY = rect.top + rect.height() * 0.38f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        paint.setColor(Color.argb(230, 255, 99, 132));
        canvas.drawArc(new RectF(centerX - dp(18), centerY - dp(8), centerX + dp(18), centerY + dp(24)),
                205, 130, false, paint);
        paint.setColor(Color.argb(230, 255, 236, 118));
        canvas.drawArc(new RectF(centerX - dp(14), centerY - dp(4), centerX + dp(14), centerY + dp(20)),
                205, 130, false, paint);
        paint.setColor(Color.argb(230, 116, 219, 214));
        canvas.drawArc(new RectF(centerX - dp(10), centerY, centerX + dp(10), centerY + dp(16)),
                205, 130, false, paint);
        paint.setStyle(Paint.Style.FILL);
        drawPropStar(canvas, centerX + dp(18), centerY - dp(6), dp(5));
    }

    private void drawCrystalCore(Canvas canvas, int row, int col, RectF rect) {
        if (crystalCore[row][col] <= 0) {
            return;
        }

        float centerX = rect.right - dp(18);
        float centerY = rect.bottom - dp(18);
        Path crystal = new Path();
        crystal.moveTo(centerX, centerY - dp(15));
        crystal.lineTo(centerX + dp(11), centerY - dp(2));
        crystal.lineTo(centerX + dp(6), centerY + dp(14));
        crystal.lineTo(centerX - dp(8), centerY + dp(12));
        crystal.lineTo(centerX - dp(12), centerY - dp(2));
        crystal.close();
        paint.setColor(Color.argb(225, 106, 225, 255));
        canvas.drawPath(crystal, paint);
        paint.setColor(Color.argb(230, 255, 236, 118));
        drawPropStar(canvas, centerX + dp(4), centerY - dp(5), dp(5));
    }

    private void drawMusicBox(Canvas canvas, int row, int col, RectF rect) {
        if (musicBox[row][col] <= 0) {
            return;
        }

        float pulse = 0.55f + 0.45f * (float) Math.sin(System.currentTimeMillis() / 190.0);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2 + pulse * 2));
        paint.setColor(Color.argb((int) (110 + pulse * 80), 255, 236, 118));
        canvas.drawRoundRect(new RectF(rect.left + dp(3), rect.top + dp(3), rect.right - dp(3), rect.bottom - dp(3)),
                dp(13), dp(13), paint);
        paint.setStyle(Paint.Style.FILL);

        float centerX = rect.left + rect.width() * 0.28f;
        float centerY = rect.bottom - dp(18);
        RectF box = new RectF(centerX - dp(13), centerY - dp(9), centerX + dp(13), centerY + dp(12));
        paint.setColor(Color.argb(230, 174, 156, 255));
        canvas.drawRoundRect(box, dp(5), dp(5), paint);
        paint.setColor(Color.argb(230, 255, 236, 118));
        canvas.drawRect(box.left + dp(4), box.top, box.right - dp(4), box.top + dp(5), paint);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.WHITE);
        // 音符轮廓让最终章奖励格更容易在密集棋盘中被认出来。
        canvas.drawLine(centerX + dp(5), centerY - dp(18), centerX + dp(5), centerY - dp(3), paint);
        canvas.drawLine(centerX + dp(5), centerY - dp(18), centerX + dp(14), centerY - dp(14), paint);
        canvas.drawCircle(centerX + dp(3), centerY, dp(4), paint);
        drawPropStar(canvas, rect.right - dp(12), rect.top + dp(12), dp(5 + pulse * 2));
        postInvalidateOnAnimation();
    }

    private void drawCountdownBomb(Canvas canvas, int row, int col, RectF rect) {
        if (countdownBomb[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.24f;
        float centerY = rect.bottom - dp(18);
        int timer = countdownBomb[row][col];
        paint.setColor(timer <= 2 ? Color.argb(230, 255, 88, 112) : Color.argb(220, 33, 37, 56));
        canvas.drawCircle(centerX, centerY, dp(13), paint);
        if (isLastCountdownBombReady()) {
            // 最后一枚炸弹用红色脉冲外圈强调，提示拆掉即可拿护盾奖励。
            float pulse = 0.55f + 0.45f * (float) Math.sin(System.currentTimeMillis() / 180.0);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2 + pulse * 2));
            paint.setColor(Color.argb((int) (135 + pulse * 95), 255, 88, 112));
            canvas.drawCircle(centerX, centerY, dp(16 + pulse * 4));
            paint.setStyle(Paint.Style.FILL);
            postInvalidateOnAnimation();
        }
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.argb(220, 255, 236, 118));
        canvas.drawLine(centerX + dp(5), centerY - dp(10), centerX + dp(10), centerY - dp(16), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(12));
        textPaint.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(timer), centerX, centerY + dp(4), textPaint);
        if (timer <= 2) {
            postInvalidateOnAnimation();
        }
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

    private void spawnComboBurst(int combo, Set<Cell> cells) {
        if (tileSize <= 0 || combo < 3 || cells.isEmpty()) {
            return;
        }

        float centerX = 0;
        float centerY = 0;
        for (Cell cell : cells) {
            centerX += boardLeft + cell.col * tileSize + tileSize / 2f;
            centerY += boardTop + cell.row * tileSize + tileSize / 2f;
        }
        centerX /= cells.size();
        centerY /= cells.size();

        int burstCount = Math.min(22, 6 + combo * 3);
        for (int i = 0; i < burstCount; i++) {
            double angle = Math.PI * 2 * i / burstCount + random.nextDouble() * 0.25;
            float speed = dp(2.5f + random.nextFloat() * (2.5f + combo * 0.2f));
            int color = palette[(i + combo) % TILE_KINDS];
            // 高连击额外喷发一圈粒子，强化连续消除的爽感。
            particles.add(new Particle(centerX, centerY,
                    (float) Math.cos(angle) * speed,
                    (float) Math.sin(angle) * speed,
                    color, dp(4 + Math.min(4, combo))));
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
        if (lastTaskRewardType == 23 && age < 900) {
            text = "奖励格连收 罗盘+" + lastRewardCellMilestoneAmount;
        } else if (lastGiftReward > 0 && age < 900) {
            text = "礼盒奖励 +" + lastGiftReward;
        } else if (lastMoveChestReward > 0 && age < 900) {
            text = "步数 +" + lastMoveChestReward;
        } else if (lastCloudReward > 0 && age < 900) {
            text = "彩云 +" + lastCloudReward;
        } else if (lastFlowerReward > 0 && age < 900) {
            text = "花苞 +" + lastFlowerReward;
        } else if (lastGoldenEggReward > 0 && age < 900) {
            text = "黄金蛋 +" + lastGoldenEggReward;
        } else if (lastCoinPouchReward > 0 && age < 900) {
            text = "金币袋 +" + lastCoinPouchReward;
        } else if (lastPaintBucketReward > 0 && age < 900) {
            text = "染色 +" + lastPaintBucketReward;
        } else if (lastWindmillReward > 0 && age < 900) {
            text = "风车横扫 x" + lastWindmillReward;
        } else if (lastJewelBowReward > 0 && age < 900) {
            text = "蝴蝶结 +" + lastJewelBowReward;
        } else if (lastStardustJarReward > 0 && age < 900) {
            text = "星尘 +" + lastStardustJarReward;
        } else if (lastWishLampReward > 0 && age < 900) {
            text = "许愿 +" + lastWishLampReward;
        } else if (lastResonanceDrumReward > 0 && age < 900) {
            text = "共鸣爆发 +" + lastResonanceDrumReward;
        } else if (lastAuroraPrismReward > 0 && age < 900) {
            text = "极光棱镜 x" + lastAuroraPrismReward;
        } else if (lastRainbowBottleReward > 0 && age < 900) {
            text = "彩虹瓶 x" + lastRainbowBottleReward;
        } else if (lastEnergyPotionReward > 0 && age < 900) {
            text = "能量 +" + lastEnergyPotionReward;
        } else if (lastButterflyReward > 0 && age < 900) {
            text = "蝴蝶助力 x" + lastButterflyReward;
        } else if (lastGemReward > 0 && age < 900) {
            text = "钻石 +" + lastGemReward;
        } else if (lastPortalReward > 0 && age < 900) {
            text = "传送门 x" + lastPortalReward;
        } else if (lastHourglassReward > 0 && age < 900) {
            text = "沙漏 +" + lastHourglassReward;
        } else if (lastLuckyStarRewardProp != NONE && age < 900) {
            text = "幸运星 " + getPropName(lastLuckyStarRewardProp);
        } else if (lastLuckyCloverRewardType > 0 && age < 900) {
            text = buildLuckyCloverRewardText();
        } else if (lastMysteryRewardType > 0 && age < 900) {
            text = buildMysteryRewardText();
        } else if (lastPearlReward > 0 && age < 900) {
            text = "珍珠 +" + lastPearlReward + " 海星镐";
        } else if (lastCarouselReward > 0 && age < 900) {
            text = "旋转木马 能量+" + lastCarouselReward;
        } else if (lastFerrisTicketReward > 0 && age < 900) {
            text = "摩天轮票根 月票+" + lastFerrisTicketReward;
        } else if (lastFireworksBarrelReward > 0 && age < 900) {
            text = "烟花桶 能量+" + lastFireworksBarrelReward;
        } else if (lastStarportBeaconReward > 0 && age < 900) {
            text = "星港信标 能量+" + lastStarportBeaconReward;
        } else if (lastMeteorTrailReward > 0 && age < 900) {
            text = "流星航线 金币+" + lastMeteorTrailReward;
        } else if (lastRainbowArcReward > 0 && age < 900) {
            text = "彩虹拱桥 能量+" + lastRainbowArcReward;
        } else if (lastCrystalCoreReward > 0 && age < 900) {
            text = "糖晶塔芯 爆炸+" + lastCrystalCoreReward;
        } else if (lastTaskRewardType == 25 && age < 900) {
            text = "音乐盒全开 星弦琴+" + lastMusicBoxMilestoneReward;
        } else if (lastMusicBoxReward > 0 && age < 900) {
            text = "音乐盒 星弦琴+" + lastMusicBoxReward;
        } else if (lastCountdownBombReward > 0 && age < 900) {
            text = "拆弹 +" + lastCountdownBombReward;
        } else if (honeySpreadCount > 0 && age < 900) {
            text = "蜂蜜蔓延";
        } else if (lastTaskRewardType == 1 && age < 900) {
            text = "收集奖励";
        } else if (lastTaskRewardType == 2 && age < 900) {
            text = "清障奖励";
        } else if (lastTaskRewardType == 3 && age < 900) {
            text = lastEnergyRewardProp == NONE ? "连击奖励" : "能量爆发 " + getPropName(lastEnergyRewardProp);
        } else if (lastTaskRewardType == 4 && age < 900) {
            text = "钥匙奖励";
        } else if (lastTaskRewardType == 5 && age < 900) {
            text = "魔棒生成";
        } else if (lastTaskRewardType == 6 && age < 900) {
            text = "特效生成";
        } else if (lastTaskRewardType == 7 && age < 900) {
            text = "净化 +" + feedbackCleared;
        } else if (lastTaskRewardType == 8 && age < 900) {
            text = "星锤生成";
        } else if (lastTaskRewardType == 9 && age < 900) {
            text = "目标刷 +" + feedbackCleared;
        } else if (lastTaskRewardType == 10 && age < 900) {
            text = "护盾生效 x" + lastShieldReward;
        } else if (lastTaskRewardType == 11 && age < 900) {
            text = "能量核心 " + getPropName(lastEnergyRewardProp);
        } else if (lastTaskRewardType == 12 && age < 900) {
            text = "破锁 +" + feedbackCleared;
        } else if (lastTaskRewardType == 13 && age < 900) {
            text = "极光球 彩虹生成";
        } else if (lastTaskRewardType == 14 && age < 900) {
            text = "海星镐 +" + feedbackCleared;
        } else if (lastTaskRewardType == 15 && age < 900) {
            text = "月光票券 +2步";
        } else if (lastTaskRewardType == 16 && age < 900) {
            text = "烟花礼炮 能量+30";
        } else if (lastTaskRewardType == 17 && age < 900) {
            text = "星轨罗盘 全线展开";
        } else if (lastTaskRewardType == 18 && age < 900) {
            text = "泡泡棒 净化+" + feedbackCleared;
        } else if (lastTaskRewardType == 19 && age < 900) {
            text = "雪花球 稳场+" + feedbackCleared;
        } else if (lastTaskRewardType == 20 && age < 900) {
            text = "星弦竖琴 能量+36";
        } else if (lastTaskRewardType == 21 && age < 900) {
            text = "金币不足 还差" + feedbackCleared;
        } else if (lastTaskRewardType == 22 && age < 900) {
            text = "购买道具 -" + feedbackCleared + "币";
        } else if (lastTaskRewardType == 24 && age < 900) {
            text = "拆弹奖励 护盾+1";
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
        canvas.drawRoundRect(new RectF(dp(34), centerY - dp(58), getWidth() - dp(34), centerY + dp(72)),
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
        if (getLevelRewardCellCount(level) >= 3) {
            // 开场明确奖励格累计收益，让玩家知道每3格可换一次罗盘补给。
            goalText += "  奖励格每3给罗盘";
        }
        if (level.cloudCount > 0) {
            goalText += "  彩云 " + level.cloudCount;
        }
        if (level.flowerCount > 0) {
            goalText += "  花苞 " + level.flowerCount;
        }
        if (level.coralReefCount > 0) {
            goalText += "  珊瑚礁 " + level.coralReefCount;
        }
        if (level.gemCount > 0) {
            goalText += "  钻石 " + level.gemCount;
        }
        if (level.goldenEggCount > 0) {
            goalText += "  黄金蛋 " + level.goldenEggCount;
        }
        if (level.coinPouchCount > 0) {
            goalText += "  金币袋 " + level.coinPouchCount;
        }
        if (level.paintBucketCount > 0) {
            goalText += "  染色桶 " + level.paintBucketCount;
        }
        if (level.windmillCount > 0) {
            goalText += "  风车 " + level.windmillCount;
        }
        if (level.jewelBowCount > 0) {
            goalText += "  蝴蝶结 " + level.jewelBowCount;
        }
        if (level.stardustJarCount > 0) {
            goalText += "  星尘罐 " + level.stardustJarCount;
        }
        if (level.wishLampCount > 0) {
            goalText += "  许愿灯 " + level.wishLampCount;
        }
        if (level.resonanceDrumCount > 0) {
            goalText += "  共鸣鼓 " + level.resonanceDrumCount;
        }
        if (level.auroraPrismCount > 0) {
            goalText += "  极光棱镜 " + level.auroraPrismCount;
        }
        if (level.rainbowBottleCount > 0) {
            goalText += "  彩虹瓶 " + level.rainbowBottleCount;
        }
        if (level.energyPotionCount > 0) {
            goalText += "  能量药水 " + level.energyPotionCount;
        }
        if (level.butterflyCount > 0) {
            goalText += "  蝴蝶 " + level.butterflyCount;
        }
        if (level.portalCount > 0) {
            goalText += "  传送门 " + level.portalCount;
        }
        if (level.hourglassCount > 0) {
            goalText += "  沙漏 " + level.hourglassCount;
        }
        if (level.luckyStarCount > 0) {
            goalText += "  幸运星 " + level.luckyStarCount;
        }
        if (level.luckyCloverCount > 0) {
            goalText += "  幸运草 " + level.luckyCloverCount;
        }
        if (level.mysteryBoxCount > 0) {
            goalText += "  神秘盒 " + level.mysteryBoxCount;
        }
        if (level.pearlCount > 0) {
            goalText += "  贝壳珍珠 " + level.pearlCount;
        }
        if (level.carouselCount > 0) {
            goalText += "  旋转木马 " + level.carouselCount;
        }
        if (level.ferrisTicketCount > 0) {
            goalText += "  摩天轮票根 " + level.ferrisTicketCount;
        }
        if (level.fireworksBarrelCount > 0) {
            goalText += "  烟花桶 " + level.fireworksBarrelCount;
        }
        if (level.starportBeaconCount > 0) {
            goalText += "  星港信标 " + level.starportBeaconCount;
        }
        if (level.meteorTrailCount > 0) {
            goalText += "  流星航线 " + level.meteorTrailCount;
        }
        if (level.rainbowArcCount > 0) {
            goalText += "  彩虹拱桥 " + level.rainbowArcCount;
        }
        if (level.crystalCoreCount > 0) {
            goalText += "  糖晶塔芯 " + level.crystalCoreCount;
        }
        if (level.musicBoxCount > 0) {
            goalText += "  音乐盒 " + level.musicBoxCount;
            // 音乐盒会补星弦琴，开场直接说明收益，方便玩家优先规划。
            goalText += "  开盒送星弦";
            goalText += "  全开再奖";
        }
        if (level.countdownBombCount > 0) {
            goalText += "  炸弹 " + level.countdownBombCount;
            // 开场提示拆弹奖励，让玩家主动处理炸弹更有动力。
            goalText += "  拆完护盾";
        }
        if (level.elite) {
            goalText += "  精英奖励";
        }
        if (level.scoreGoal > 0) {
            goalText += "  高分 " + level.scoreGoal;
        }
        if (isHiddenChallengeLevel()) {
            goalText += levelHiddenChallengesCleared[levelIndex] ? "  隐藏已达成"
                    : "  隐藏步限 " + Math.max(7, level.moves - 4);
        }
        if (lastComebackAssistMoves > 0) {
            goalText += "  助力 +" + lastComebackAssistMoves + "步";
            if (lastComebackAssistProp != NONE) {
                goalText += " " + getPropName(lastComebackAssistProp) + "+" + lastComebackAssistAmount;
            }
        }
        if (!dailyChallengeMode && levelRanks[levelIndex] > 0) {
            goalText += "  最佳" + buildRankText(levelRanks[levelIndex]);
        }
        String replayGoal = buildLevelIntroReplayGoal();
        if (replayGoal.length() > 0) {
            goalText += "  " + replayGoal;
        }
        RectF goalRect = new RectF(dp(42), centerY + dp(18), getWidth() - dp(42), centerY + dp(44));
        drawTextFit(canvas, goalText, goalRect, 15, Color.WHITE);
        String strategyHint = buildLevelStrategyHint(level);
        if (strategyHint.length() > 0) {
            RectF strategyRect = new RectF(dp(42), centerY + dp(44), getWidth() - dp(42), centerY + dp(66));
            drawTextFit(canvas, strategyHint, strategyRect, 13, Color.rgb(255, 236, 133));
        }
        postInvalidateOnAnimation();
    }

    private String buildLevelStrategyHint(Level level) {
        if (!dailyChallengeMode && levelRanks[levelIndex] >= 4 && !levelPerfectCleared[levelIndex]) {
            return "策略 控步数留爆炸冲完美";
        } else if (level.countdownBombCount > 0) {
            return "策略 先拆炸弹拿护盾";
        } else if (level.keyCount > 0) {
            return "策略 火箭/罗盘优先抢钥匙";
        } else if (level.musicBoxCount > 0) {
            return "策略 优先开音乐盒铺连击";
        } else if (getRewardCellCount() >= 3) {
            // 奖励格密集时优先提示精准道具，帮助玩家把额外收益转成通关优势。
            return "策略 火箭/罗盘优先收奖励";
        } else if (level.chainCount > 0) {
            return "策略 破锁先开链";
        } else if (level.honeyCount > 0) {
            return "策略 冻结/雪花球控蜂蜜";
        } else if (getLevelObstacleCount(level) >= 24) {
            return "策略 净化/海星镐优先清障";
        } else if (level.targetAmount >= 22) {
            return "策略 磁铁/目标刷补收集";
        } else if (level.comboGoal > 0) {
            return "策略 魔棒/星弦琴铺连击";
        } else if (level.scoreGoal > 0) {
            return "策略 礼炮/罗盘冲高分";
        } else if (level.moveLimitGoal > 0) {
            return "策略 加步/时钟保步限";
        } else if (isHiddenChallengeLevel()) {
            return "策略 时钟/加步保隐藏步限";
        } else if (level.elite) {
            return "策略 留爆炸道具给尾盘";
        }
        // 开局策略提示只给最关键方向，避免遮住关卡目标信息。
        return "";
    }

    private String buildLevelIntroReplayGoal() {
        if (dailyChallengeMode || levelStars[levelIndex] <= 0) {
            return "";
        }
        if (levelStars[levelIndex] < 3) {
            return "再冲" + (3 - levelStars[levelIndex]) + "星";
        }
        String challenge = buildReplayChallengeReason(levelIndex);
        if (challenge.length() > 0) {
            // 回访开场直接提示具体挑战，避免只看到泛化评级目标。
            return challenge;
        }
        if (levelRanks[levelIndex] < 4) {
            return "再冲" + buildRankText(4);
        }
        if (levelRanks[levelIndex] >= 4 && !levelPerfectCleared[levelIndex]) {
            return "再冲完美";
        }
        return "";
    }

    private int getLevelObstacleCount(Level level) {
        return level.iceCount + level.honeyCount + level.stoneCount + level.vineCount + level.chainCount
                + level.shellCount + level.coralReefCount + level.flowerCount;
    }

    private int getLevelRewardCellCount(Level level) {
        return level.giftCount + level.cloudCount + level.gemCount + level.goldenEggCount + level.coinPouchCount
                + level.paintBucketCount + level.windmillCount + level.jewelBowCount + level.stardustJarCount
                + level.wishLampCount + level.resonanceDrumCount + level.auroraPrismCount + level.rainbowBottleCount
                + level.energyPotionCount + level.butterflyCount + level.portalCount + level.hourglassCount
                + level.luckyStarCount + level.luckyCloverCount + level.mysteryBoxCount + level.pearlCount
                + level.carouselCount + level.ferrisTicketCount + level.fireworksBarrelCount + level.starportBeaconCount
                + level.meteorTrailCount + level.rainbowArcCount + level.crystalCoreCount + level.musicBoxCount;
    }

    private void showFeedback(int combo, int cleared) {
        feedbackCombo = combo;
        feedbackCleared = cleared;
        feedbackStartTime = System.currentTimeMillis();
    }

    private void showNormalFeedback(int combo, int cleared) {
        lastTaskRewardType = 0;
        lastEnergyRewardProp = NONE;
        lastCountdownBombReward = 0;
        showFeedback(combo, cleared);
    }

    private String buildMysteryRewardText() {
        if (lastMysteryRewardType == 1) {
            return "神秘盒 分数+" + lastMysteryRewardAmount;
        } else if (lastMysteryRewardType == 2) {
            return "神秘盒 金币+" + lastMysteryRewardAmount;
        } else if (lastMysteryRewardType == 3) {
            return "神秘盒 步数+" + lastMysteryRewardAmount;
        }
        return "神秘盒 " + getPropName(lastMysteryRewardProp);
    }

    private String buildLuckyCloverRewardText() {
        if (lastLuckyCloverRewardType == 1) {
            return "幸运草 金币+" + lastLuckyCloverRewardAmount;
        } else if (lastLuckyCloverRewardType == 2) {
            return "幸运草 步数+" + lastLuckyCloverRewardAmount;
        } else if (lastLuckyCloverRewardType == 3) {
            return "幸运草 能量+" + lastLuckyCloverRewardAmount;
        }
        return "幸运草 " + getPropName(lastLuckyCloverRewardProp);
    }

    private String buildFireworksChapterRewardText() {
        return isFireworksChapter(getChapterIndex(levelIndex)) ? " 礼炮+1" : "";
    }

    private String buildFireworksChapterCompassText() {
        return isFireworksChapter(getChapterIndex(levelIndex)) ? " 罗盘+1" : "";
    }

    private String buildChapterMasteryPropRewardText() {
        int chapter = getChapterIndex(levelIndex);
        if (isRainbowValleyChapter(chapter)) {
            return " 极光+1";
        } else if (isCrystalTowerChapter(chapter)) {
            return " 罗盘+1";
        } else if (isBubbleGalaxyChapter(chapter)) {
            return " 极光+1 罗盘+1 泡泡棒+1";
        } else if (isMintFireworksChapter(chapter)) {
            return " 礼炮+1 净化+1";
        } else if (isFrostCandyChapter(chapter)) {
            return " 罗盘+1 极光+1";
        } else if (isGlassCloudChapter(chapter)) {
            return " 泡泡棒+1 礼炮+1";
        } else if (isHoneyClockChapter(chapter)) {
            return " 时钟+1 雪花球+1";
        } else if (isPrismSongChapter(chapter)) {
            return " 极光+1 罗盘+1 星弦琴+1";
        } else if (isStarHarpStageChapter(chapter)) {
            return " 星弦琴+2 极光+1";
        } else if (isCandyTheaterChapter(chapter)) {
            return " 星弦琴+1 雪花球+1 礼炮+1";
        }
        return buildFireworksChapterRewardText();
    }

    private String buildChapterElitePropRewardText() {
        int chapter = getChapterIndex(levelIndex);
        if (isRainbowValleyChapter(chapter)) {
            return " 罗盘+1";
        } else if (isCrystalTowerChapter(chapter)) {
            return " 礼炮+1";
        } else if (isBubbleGalaxyChapter(chapter)) {
            return " 极光+1 泡泡棒+1";
        } else if (isMintFireworksChapter(chapter)) {
            return " 礼炮+1 罗盘+1";
        } else if (isFrostCandyChapter(chapter)) {
            return " 罗盘+1 泡泡棒+1";
        } else if (isGlassCloudChapter(chapter)) {
            return " 礼炮+1 极光+1";
        } else if (isHoneyClockChapter(chapter)) {
            return " 时钟+1 礼炮+1";
        } else if (isPrismSongChapter(chapter)) {
            return " 极光+1 星弦琴+1";
        } else if (isStarHarpStageChapter(chapter)) {
            return " 星弦琴+1 罗盘+1";
        } else if (isCandyTheaterChapter(chapter)) {
            return " 星弦琴+1 泡泡棒+1";
        }
        return buildFireworksChapterRewardText();
    }

    private String buildChapterRankPropRewardText() {
        int chapter = getChapterIndex(levelIndex);
        if (isRainbowValleyChapter(chapter)) {
            return " 极光+1 罗盘+1";
        } else if (isCrystalTowerChapter(chapter)) {
            return " 礼炮+1 罗盘+1";
        } else if (isBubbleGalaxyChapter(chapter)) {
            return " 极光+1 罗盘+1 泡泡棒+1";
        } else if (isMintFireworksChapter(chapter)) {
            return " 礼炮+1 罗盘+1";
        } else if (isFrostCandyChapter(chapter)) {
            return " 罗盘+2 泡泡棒+1";
        } else if (isGlassCloudChapter(chapter)) {
            return " 礼炮+1 极光+1 泡泡棒+1";
        } else if (isHoneyClockChapter(chapter)) {
            return " 时钟+2 雪花球+1";
        } else if (isPrismSongChapter(chapter)) {
            return " 极光+1 罗盘+1 星弦琴+1";
        } else if (isStarHarpStageChapter(chapter)) {
            return " 星弦琴+2 罗盘+1";
        } else if (isCandyTheaterChapter(chapter)) {
            return " 星弦琴+1 雪花球+1 泡泡棒+1";
        }
        return buildFireworksChapterRewardText() + buildFireworksChapterCompassText();
    }

    private String buildWinStreakPropRewardText() {
        return lastWinStreakRewardProp == NONE ? "" : " " + getPropName(lastWinStreakRewardProp) + "+" + lastWinStreakRewardAmount;
    }

    private String buildAchievementPropRewardText() {
        return lastAchievementRewardProp == NONE ? "" : " " + getPropName(lastAchievementRewardProp) + "+" + lastAchievementRewardAmount;
    }

    private String buildSeasonPropRewardText() {
        return lastSeasonRewardProp == NONE ? "" : " " + getPropName(lastSeasonRewardProp) + "+" + lastSeasonRewardAmount;
    }

    private String buildReplayRewardText() {
        return lastReplayRewardProp == NONE ? "" : " " + getPropName(lastReplayRewardProp) + "+" + lastReplayRewardAmount;
    }

    private String buildRankUpgradeRewardText() {
        return lastRankUpgradeRewardProp == NONE ? "" : " " + getPropName(lastRankUpgradeRewardProp) + "+" + lastRankUpgradeRewardAmount;
    }

    private String buildPerfectRewardText() {
        return lastPerfectRewardProp == NONE ? "" : " " + getPropName(lastPerfectRewardProp) + "+" + lastPerfectRewardAmount;
    }

    private String buildHiddenRewardText() {
        return lastHiddenRewardProp == NONE ? "" : " " + getPropName(lastHiddenRewardProp) + "+" + lastHiddenRewardAmount;
    }

    private List<String> buildRewardLines() {
        List<String> lines = new ArrayList<>();
        lines.add("金币 +" + lastCoinReward);
        addRewardLine(lines, "首通", lastFirstClearReward, "");
        addRewardLine(lines, "回访", lastReplayReward, buildReplayRewardText());
        addRewardLine(lines, "满星", lastFullStarReward, lastFullStarReward > 0 ? " 净化+1" : "");
        addRewardLine(lines, "补星", lastStarUpgradeReward, "");
        addRewardLine(lines, "评级", lastRankUpgradeReward, buildRankUpgradeRewardText());
        addRewardLine(lines, "完美", lastPerfectReward, buildPerfectRewardText());
        if (lastPerfectRetained) {
            lines.add("完美保持");
        }
        addRewardLine(lines, "隐藏", lastHiddenReward, buildHiddenRewardText());
        addRewardLine(lines, "精英", lastEliteReward, "");
        addRewardLine(lines, "成就", lastAchievementReward, buildAchievementPropRewardText());
        addRewardLine(lines, "连胜", lastWinStreakReward, buildWinStreakPropRewardText());
        addRewardLine(lines, "赛季", lastSeasonReward, buildSeasonPropRewardText());
        addRewardLine(lines, "满星大师", lastChapterMasteryReward,
                lastChapterMasteryReward > 0 ? " 净化+1" + buildChapterMasteryPropRewardText() : "");
        addRewardLine(lines, "章节精英", lastChapterEliteReward,
                lastChapterEliteReward > 0 ? " 流星+1" + buildChapterElitePropRewardText() : "");
        addRewardLine(lines, "章节评级", lastChapterRankReward,
                lastChapterRankReward > 0 ? " 潮汐+1" + buildChapterRankPropRewardText() : "");
        addRewardLine(lines, "章节隐藏", lastChapterHiddenReward,
                lastChapterHiddenReward > 0 ? " 时钟+1 罗盘+1" : "");
        addRewardLine(lines, "章节完美", lastChapterPerfectReward,
                lastChapterPerfectReward > 0 ? " 星弦琴+1 罗盘+1" : "");
        if (rewardBombMilestone > 0) {
            // 拆弹护盾属于局内阶段奖励，也显示到结算明细里。
            lines.add("拆弹奖励 护盾+1");
        }
        if (lastMusicBoxReward > 0) {
            // 终章音乐盒的星弦琴补给也显示到奖励明细，避免被普通消除反馈吞掉。
            lines.add("音乐盒 星弦琴+" + lastMusicBoxReward);
        }
        if (lastMusicBoxMilestoneReward > 0) {
            // 全开音乐盒的额外储备奖励进入结算明细，形成明确的收集闭环。
            lines.add("音乐盒全开 星弦琴+" + lastMusicBoxMilestoneReward);
        }
        if (lastDailyChallengeMilestoneProp != NONE) {
            lines.add("每日连胜 " + getPropName(lastDailyChallengeMilestoneProp) + "+" + lastDailyChallengeMilestoneAmount);
        }
        trimRewardLines(lines);
        return lines;
    }

    private void trimRewardLines(List<String> lines) {
        int hiddenCount = Math.max(0, lines.size() - 4);
        while (lines.size() > 4) {
            // 奖励过多时优先保留金币和后续高价值稀有奖励，避免章节/完美奖励被挤掉。
            lines.remove(lines.size() > 5 ? 1 : 2);
        }
        if (hiddenCount > 0 && lines.size() >= 4) {
            lines.set(3, lines.get(3) + "  更多+" + hiddenCount);
        }
    }

    private void addRewardLine(List<String> lines, String label, int amount, String extra) {
        if (amount > 0 || extra.length() > 0) {
            lines.add(label + "+" + amount + extra);
        }
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
        if (levelComplete && (lastAchievementReward > 0 || lastStarUpgradeReward > 0 || lastRankUpgradeReward > 0
                || lastPerfectReward > 0 || lastPerfectRetained || lastHiddenReward > 0 || lastWinStreakReward > 0
                || lastEliteReward > 0 || lastFirstClearReward > 0 || lastFullStarReward > 0
                || lastReplayReward > 0
                || lastChapterMasteryReward > 0 || lastChapterEliteReward > 0 || lastChapterRankReward > 0
                || lastChapterHiddenReward > 0 || lastChapterPerfectReward > 0
                || lastSeasonReward > 0 || lastDailyChallengeMilestoneProp != NONE
                || rewardCellClearedCount >= 3 || rewardBombMilestone > 0 || lastMusicBoxReward > 0
                || lastMusicBoxMilestoneReward > 0)) {
            drawRewardSparkles(canvas, getWidth() / 2f, getHeight() * 0.42f - dp(12));
        }

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(28));
        canvas.drawText(levelComplete ? "闯关成功" : "再试一次", getWidth() / 2f, getHeight() * 0.42f, textPaint);
        textPaint.setTextSize(sp(16));
        if (levelComplete) {
            String bonusText = buildStars(lastStars) + "  评级 " + buildRankText(lastRank) + "  步数奖励 +" + lastBonusScore;
            if (challengeCleared) {
                bonusText += "  挑战达成";
            }
            if (comboChallengeCleared) {
                bonusText += "  连击达成";
            }
            if (scoreChallengeCleared) {
                bonusText += "  高分达成";
            }
            if (hiddenChallengeCleared) {
                bonusText += "  隐藏达成";
            }
            if (lastEliteReward > 0) {
                bonusText += "  精英通关";
            }
            if (lastFirstClearReward > 0) {
                bonusText += "  首次通关";
            }
            if (lastReplayReward > 0) {
                bonusText += "  回访提升";
            }
            if (lastFullStarReward > 0) {
                bonusText += "  满星达成";
            }
            if (lastPerfectReward > 0) {
                bonusText += "  完美通关";
            } else if (lastPerfectRetained) {
                bonusText += "  完美保持";
            }
            if (lastChapterEliteReward > 0) {
                bonusText += "  章节精英";
            }
            if (lastChapterRankReward > 0) {
                bonusText += "  章节评级";
            }
            if (lastChapterHiddenReward > 0) {
                bonusText += "  章节隐藏";
            }
            if (lastChapterPerfectReward > 0) {
                bonusText += "  章节完美";
            }
            if (lastSeasonReward > 0) {
                bonusText += "  赛季任务";
            }
            if (rewardCellClearedCount >= 3) {
                // 结算页回看本局奖励格收益，强化主动收奖励格的成就感。
                bonusText += "  奖励格" + rewardCellClearedCount;
            }
            if (rewardBombMilestone > 0) {
                // 拆完炸弹的护盾奖励也进入结算摘要，强化高压关卡正反馈。
                bonusText += "  拆弹护盾";
            }
            if (lastMusicBoxReward > 0) {
                // 音乐盒带来的星弦琴补给进入结算摘要，强化终章奖励格追求。
                bonusText += "  音乐盒" + lastMusicBoxReward;
            }
            if (lastMusicBoxMilestoneReward > 0) {
                bonusText += "  音乐盒全开";
            }
            drawTextFit(canvas, bonusText, new RectF(dp(24), getHeight() * 0.475f,
                    getWidth() - dp(24), getHeight() * 0.505f), 16, Color.WHITE);
            drawChallengeBadges(canvas, getWidth() / 2f, getHeight() * 0.515f);
            String scoreText = dailyChallengeMode ? "挑战分 " + score : "最佳分 " + levelBestScores[levelIndex];
            canvas.drawText(scoreText, getWidth() / 2f, getHeight() * 0.55f, textPaint);
            RectF nextGoalRect = new RectF(dp(34), getHeight() * 0.565f, getWidth() - dp(34), getHeight() * 0.592f);
            drawTextFit(canvas, buildSuccessNextGoalText(), nextGoalRect, 14, Color.rgb(255, 236, 133));
            drawRewardLines(canvas);
        } else {
            String failText;
            if (countdownBombExploded) {
                failText = pickContinueProp() == NONE ? "炸弹爆炸，点击重试" : "炸弹爆炸，点击消耗" + getPropName(pickContinueProp()) + "续步";
            } else if (coins >= CONTINUE_COST) {
                failText = "点击续步 -10金币";
            } else if (pickContinueProp() != NONE) {
                failText = "金币不足，点击消耗" + getPropName(pickContinueProp()) + "续步";
            } else if (dailyChallengeMode) {
                failText = "金币不足，返回主线";
            } else {
                failText = "金币不足，点击重试";
            }
            canvas.drawText(failText, getWidth() / 2f, getHeight() * 0.49f, textPaint);
            String assistText = buildComebackAssistPreviewText();
            if (assistText.length() > 0) {
                textPaint.setTextSize(sp(14));
                canvas.drawText(assistText, getWidth() / 2f, getHeight() * 0.55f, textPaint);
            }
            RectF failProgressRect = new RectF(dp(34), getHeight() * (assistText.length() > 0 ? 0.565f : 0.532f),
                    getWidth() - dp(34), getHeight() * (assistText.length() > 0 ? 0.598f : 0.565f));
            drawTextFit(canvas, buildFailureProgressText(), failProgressRect, 14, Color.WHITE);
            String propAdviceText = buildFailurePropAdviceText();
            if (propAdviceText.length() > 0) {
                RectF propAdviceRect = new RectF(dp(34), getHeight() * (assistText.length() > 0 ? 0.6f : 0.568f),
                        getWidth() - dp(34), getHeight() * (assistText.length() > 0 ? 0.63f : 0.598f));
                drawTextFit(canvas, propAdviceText, propAdviceRect, 13, Color.rgb(255, 236, 133));
            }
            String replayText = buildFailureReplayText();
            if (replayText.length() > 0) {
                RectF replayRect = new RectF(dp(34), getHeight() * (assistText.length() > 0 ? 0.632f : 0.6f),
                        getWidth() - dp(34), getHeight() * (assistText.length() > 0 ? 0.662f : 0.63f));
                drawTextFit(canvas, replayText, replayRect, 12, Color.WHITE);
            }
        }
    }

    private boolean hasClaimableMapRewardAfterClear() {
        int chapter = getChapterIndex(levelIndex);
        return (!dailyGoalClaimed && dailyGoalProgress >= 6) || canClaimChapterChest(chapter)
                || getAvailableStarChests() > 0 || getAvailableRankChests() > 0;
    }

    private String buildFailureProgressText() {
        Level level = levels.get(levelIndex);
        StringBuilder text = new StringBuilder();
        appendFailureProgressPart(text, "收集差", targetRemaining);
        appendFailureProgressPart(text, "清障差", getCurrentObstacleRemaining(level));
        appendFailureProgressPart(text, "分数差", Math.max(0, level.targetScore - score));
        appendFailureProgressPart(text, "奖励剩", getRewardCellCount());
        if (isRewardCellMilestoneNear()) {
            // 失败页标出奖励格里程碑只差1格，方便下局优先补拿罗盘。
            appendFailureProgressPart(text, "罗盘差", 1);
        }
        if (level.countdownBombCount > 0) {
            appendFailureProgressPart(text, "炸弹剩", getCountdownBombRemainingCount());
            appendFailureProgressPart(text, "炸弹急", getCountdownBombUrgency());
        }
        if (level.musicBoxCount > 0) {
            // 失败复盘也显示音乐盒剩余，提醒下局优先拿星弦琴储备。
            appendFailureProgressPart(text, "音乐盒剩", getMusicBoxRemainingCount());
        }
        if (level.moveLimitGoal > 0 && !isMoveLimitGoalCleared(level)) {
            appendFailureProgressPart(text, "步限超", movesUsed - getMoveLimitGoal(level));
        }
        if (level.comboGoal > 0 && !isComboGoalCleared(level)) {
            appendFailureProgressPart(text, "连击差", level.comboGoal - bestCombo);
        }
        if (level.scoreGoal > 0 && !isScoreGoalCleared(level)) {
            appendFailureProgressPart(text, "高分差", level.scoreGoal - score);
        }
        if (isHiddenChallengeLevel() && !hiddenChallengeCleared && !levelHiddenChallengesCleared[levelIndex]) {
            appendFailureProgressPart(text, "隐藏超", movesUsed - Math.max(7, level.moves - 4));
        }
        if (!dailyChallengeMode && levelRanks[levelIndex] >= 4 && !levelPerfectCleared[levelIndex]) {
            appendFailureProgressPart(text, "完美差", getPerfectFailureGap(level));
        }
        // 失败页给出最短追踪目标，帮助玩家判断下局优先用哪类道具。
        return text.length() == 0 ? "已接近通关，续步可冲过" : text.toString();
    }

    private int getPerfectFailureGap(Level level) {
        // 失败时lastRank尚未刷新，完美差只提示步数缺口，避免误导玩家。
        return Math.max(1, movesUsed - Math.max(6, level.moves / 2));
    }

    private int getCountdownBombUrgency() {
        int urgency = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (countdownBomb[row][col] > 0) {
                    // 数值越大表示越紧急，失败页用于提醒下局优先处理炸弹格。
                    urgency = Math.max(urgency, 6 - countdownBomb[row][col]);
                }
            }
        }
        return urgency;
    }

    private int getCountdownBombRemainingCount() {
        int count = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (countdownBomb[row][col] > 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private int getMusicBoxRemainingCount() {
        int count = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (musicBox[row][col] > 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private int getLowestCountdownBombTimer() {
        int timer = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (countdownBomb[row][col] > 0) {
                    // HUD显示最短倒计时，帮助玩家快速判断是否需要立刻处理炸弹。
                    timer = timer == 0 ? countdownBomb[row][col] : Math.min(timer, countdownBomb[row][col]);
                }
            }
        }
        return timer;
    }

    private void appendFailureProgressPart(StringBuilder text, String label, int amount) {
        if (amount <= 0) {
            return;
        }
        if (text.length() > 0) {
            text.append("  ");
        }
        text.append(label).append(amount);
    }

    private int getCurrentObstacleRemaining(Level level) {
        return iceRemaining + honeyRemaining + stoneRemaining + vineRemaining + chainRemaining
                + shellRemaining + coralReefRemaining + flowerRemaining + keyRemaining;
    }

    private String buildFailureReplayText() {
        if (dailyChallengeMode) {
            return "";
        }
        int replayLevel = findReplayTargetLevel();
        if (replayLevel < 0 || replayLevel == levelIndex) {
            return "";
        }
        // 失败页补关提示带上章节名，方便玩家快速定位要回访的地图区域。
        return "回访可补 " + chapterNames[getChapterIndex(replayLevel)] + " 第" + (replayLevel + 1)
                + "关 " + buildReplayReason(replayLevel);
    }

    private String buildSuccessNextGoalText() {
        if (dailyChallengeMode) {
            return "下一目标 回到主线继续推进";
        }
        if (!dailyGoalClaimed && dailyGoalProgress >= 6) {
            return "下一目标 每日目标可领取";
        }
        String dailyGoalText = buildDailyGoalNextGoalText();
        if (dailyGoalText.length() > 0) {
            return dailyGoalText;
        }
        String dailyChallengeGoalText = buildDailyChallengeNextGoalText();
        if (dailyChallengeGoalText.length() > 0) {
            return dailyChallengeGoalText;
        }
        int chapter = getChapterIndex(levelIndex);
        if (lastChapterPerfectReward > 0) {
            return chapterNames[chapter] + "完美奖励已入账";
        }
        if (rewardBombMilestone > 0) {
            // 拆弹奖励后提示护盾用途，把高压关奖励接到下一局策略。
            return "下一目标 护盾留给高压炸弹关";
        }
        if (lastMusicBoxMilestoneReward > 0) {
            return "下一目标 全开星弦琴已入储备";
        }
        if (lastMusicBoxReward > 0) {
            // 音乐盒奖励后提示星弦琴用途，把奖励转成下一局主动连击目标。
            return "下一目标 星弦琴已入储备";
        }
        String rewardCellGoalText = buildRewardCellNextGoalText();
        if (rewardCellGoalText.length() > 0) {
            return rewardCellGoalText;
        }
        if (canClaimChapterChest(chapter)) {
            return "下一目标 " + chapterNames[chapter] + "宝箱可领取";
        }
        String chapterChestGoalText = buildChapterChestNextGoalText(chapter);
        if (chapterChestGoalText.length() > 0) {
            return chapterChestGoalText;
        }
        if (getAvailableStarChests() > 0) {
            return "下一目标 星级宝箱可领取";
        }
        if (getAvailableRankChests() > 0) {
            return "下一目标 评级宝箱可领取";
        }
        String chestGoalText = buildChestNextGoalText();
        if (chestGoalText.length() > 0) {
            return chestGoalText;
        }
        String winStreakGoalText = buildWinStreakNextGoalText();
        if (winStreakGoalText.length() > 0) {
            return winStreakGoalText;
        }
        String achievementGoalText = buildAchievementNextGoalText();
        if (achievementGoalText.length() > 0) {
            return achievementGoalText;
        }
        String seasonGoalText = buildSeasonNextGoalText();
        if (seasonGoalText.length() > 0) {
            return seasonGoalText;
        }
        String masteryGoalText = buildChapterMasteryNextGoalText(chapter);
        if (masteryGoalText.length() > 0) {
            return masteryGoalText;
        }
        String rankGoalText = buildChapterRankNextGoalText(chapter);
        if (rankGoalText.length() > 0) {
            return rankGoalText;
        }
        String eliteGoalText = buildChapterEliteNextGoalText(chapter);
        if (eliteGoalText.length() > 0) {
            return eliteGoalText;
        }
        String hiddenGoalText = buildChapterHiddenNextGoalText(chapter);
        if (hiddenGoalText.length() > 0) {
            return hiddenGoalText;
        }
        String perfectGoalText = buildChapterPerfectNextGoalText(chapter);
        if (perfectGoalText.length() > 0) {
            return perfectGoalText;
        }
        int nextLevel = Math.min(levelIndex + 1, levels.size() - 1);
        int replayLevel = findReplayTargetLevel();
        if (levelIndex < levels.size() - 1 && nextLevel <= highestUnlockedLevel) {
            return "下一目标 第" + (nextLevel + 1) + "关";
        }
        if (replayLevel >= 0) {
            return "回访推荐 " + chapterNames[getChapterIndex(replayLevel)] + " 第" + (replayLevel + 1)
                    + "关 " + buildReplayReason(replayLevel);
        }
        // 通关页给一个明确的下一步，减少玩家在主线和补星之间的选择成本。
        return "下一目标 收集满星和高评级";
    }

    private String buildRewardCellNextGoalText() {
        if (rewardCellClearedCount <= 0 || rewardCellClearedCount % 3 == 0) {
            return "";
        }
        // 结算页复盘本局奖励格进度，避免误导为跨关累计。
        return "本局已收" + rewardCellClearedCount + "奖励格 每3给罗盘";
    }

    private String buildDailyGoalNextGoalText() {
        if (dailyGoalClaimed) {
            return "";
        }

        int missingStars = 6 - dailyGoalProgress;
        if (missingStars > 0 && missingStars <= 2) {
            // 每日目标临近完成时优先提示，强化当天继续补星的回访动力。
            return "下一目标 每日目标差" + missingStars + "星";
        }
        return "";
    }

    private String buildDailyChallengeNextGoalText() {
        if (prefs.getLong(KEY_DAILY_CHALLENGE_DAY, -1L) == getToday()) {
            return "";
        }

        int nextStreak = dailyChallengeStreak <= 0 ? 1 : dailyChallengeStreak + 1;
        int prop = getDailyChallengeMilestoneProp(nextStreak);
        // 主线结算提醒今日挑战，把每日回访奖励接到当前游戏循环。
        return prop == NONE ? "下一目标 每日挑战奖励"
                : "下一目标 每日挑战奖" + getPropName(prop);
    }

    private String buildChestNextGoalText() {
        int starMissing = getNextStarChestTarget() - getTotalStars();
        int rankMissing = getNextRankChestTarget() - getTotalRankScore();
        if (starMissing > 0 && starMissing <= 6) {
            // 结算页提示宝箱差额，把补星和冲评级的短线奖励显示出来。
            return "下一目标 星箱差" + starMissing + "星";
        }
        if (rankMissing > 0 && rankMissing <= 8) {
            return "下一目标 评级箱差" + rankMissing;
        }
        return "";
    }

    private String buildWinStreakNextGoalText() {
        int nextStreak = getNextWinStreakMilestone();
        int missingStreak = nextStreak - winStreak;
        if (nextStreak == NONE || missingStreak <= 0 || missingStreak > 2) {
            return "";
        }

        int prop = getWinStreakMilestoneProp(nextStreak);
        // 结算页提示临近连胜节点，让连续闯关的稀有道具奖励更可见。
        return "下一目标 连胜差" + missingStreak + "奖" + getPropName(prop);
    }

    private String buildAchievementNextGoalText() {
        int nextIndex = getNextAchievementRewardIndex();
        if (nextIndex == NONE) {
            return "";
        }

        int missing = nextIndex - getClaimedAchievementCount();
        if (missing > 0 && missing <= 2) {
            // 结算页提示临近的高价值成就节点，延长满星和评级后的追求。
            return "下一目标 成就差" + missing + "奖" + getPropName(getAchievementRewardProp(nextIndex));
        }
        return "";
    }

    private String buildChapterChestNextGoalText(int chapter) {
        if (chapterChestClaimed[chapter]) {
            return "";
        }

        int missingStars = CHAPTER_CHEST_STARS - getChapterStars(chapter);
        if (missingStars > 0 && missingStars <= 6) {
            // 章节宝箱临近时在结算页提示，推动玩家回头补齐本章星数。
            return "下一目标 " + chapterNames[chapter] + "章箱差" + missingStars + "星";
        }
        return "";
    }

    private String buildSeasonNextGoalText() {
        int levelMissing = getNextSeasonLevelTarget() - seasonLevels;
        int starMissing = getNextSeasonStarTarget() - seasonStars;
        if (levelMissing <= 0 || starMissing <= 0) {
            return "下一目标 赛季奖励可冲";
        }
        if (levelMissing <= 2 || starMissing <= 6) {
            // 通关后提示赛季差额，把日常推进和补星都接到长期奖励。
            return starMissing <= levelMissing * 3 ? "下一目标 赛季差" + starMissing + "星"
                    : "下一目标 赛季差" + levelMissing + "关";
        }
        return "";
    }

    private String buildChapterMasteryNextGoalText(int chapter) {
        if (chapterMasteryClaimed[chapter] || getChapterUnlockedCount(chapter) < CHAPTER_SIZE) {
            return "";
        }

        int starMissing = CHAPTER_SIZE * 3 - getChapterStars(chapter);
        if (starMissing <= 0) {
            return "下一目标 " + chapterNames[chapter] + "满星大师";
        }
        if (starMissing <= 6) {
            // 结算页补上章节满星差额，让补星目标和大师奖励更容易衔接。
            return "下一目标 " + chapterNames[chapter] + "满星差" + starMissing;
        }
        return "";
    }

    private String buildChapterRankNextGoalText(int chapter) {
        if (chapterRankClaimed[chapter] || getChapterUnlockedCount(chapter) < CHAPTER_SIZE / 2) {
            return "";
        }

        int rankMissing = getChapterRankRewardTarget() - getChapterRankScore(chapter);
        if (rankMissing <= 0) {
            return "下一目标 " + chapterNames[chapter] + "评级奖励";
        }
        if (rankMissing <= 8) {
            // 通关后提示章节评级差额，推动玩家回头冲S/SS/SSS。
            return "下一目标 " + chapterNames[chapter] + "评级差" + rankMissing;
        }
        return "";
    }

    private String buildChapterEliteNextGoalText(int chapter) {
        int eliteCount = getChapterEliteCount(chapter);
        if (chapterEliteClaimed[chapter] || eliteCount <= 0 || getChapterUnlockedCount(chapter) < CHAPTER_SIZE) {
            return "";
        }

        int clearedElite = getChapterClearedEliteCount(chapter);
        if (clearedElite >= eliteCount) {
            return "下一目标 " + chapterNames[chapter] + "精英奖励";
        }
        if (eliteCount - clearedElite <= 1) {
            // 通关后提示章节精英差额，让阶段性高难关更容易回访。
            return "下一目标 " + chapterNames[chapter] + "精英差" + (eliteCount - clearedElite);
        }
        return "";
    }

    private String buildChapterHiddenNextGoalText(int chapter) {
        int hiddenCount = getChapterHiddenChallengeCount(chapter);
        if (chapterHiddenClaimed[chapter] || hiddenCount <= 0 || getChapterUnlockedCount(chapter) < CHAPTER_SIZE) {
            return "";
        }

        int clearedHidden = getChapterClearedHiddenChallengeCount(chapter);
        if (clearedHidden >= hiddenCount) {
            return "下一目标 " + chapterNames[chapter] + "隐藏奖励";
        }
        if (hiddenCount - clearedHidden <= 1) {
            // 通关后提示章节隐藏挑战差额，把老关回访目标接到结算页。
            return "下一目标 " + chapterNames[chapter] + "隐藏差" + (hiddenCount - clearedHidden);
        }
        return "";
    }

    private String buildChapterPerfectNextGoalText(int chapter) {
        if (chapterPerfectClaimed[chapter] || getChapterUnlockedCount(chapter) < CHAPTER_SIZE) {
            return "";
        }

        int perfectMissing = getChapterUnlockedCount(chapter) - getChapterPerfectClearCount(chapter);
        if (perfectMissing <= 0) {
            return "下一目标 " + chapterNames[chapter] + "完美奖励";
        }
        if (perfectMissing <= 2) {
            // 完美差额出现在结算页，给高评级玩家一个更清晰的回访终点。
            return "下一目标 " + chapterNames[chapter] + "完美差" + perfectMissing;
        }
        return "";
    }

    private void drawRewardLines(Canvas canvas) {
        List<String> lines = buildRewardLines();
        float startY = getHeight() * (levelComplete ? 0.62f : 0.6f);
        textPaint.setTextSize(sp(14));
        for (int i = 0; i < lines.size(); i++) {
            canvas.drawText(lines.get(i), getWidth() / 2f, startY + i * dp(18), textPaint);
        }
        textPaint.setTextSize(sp(13));
        canvas.drawText(dailyChallengeMode ? "点击返回主线" : "点击继续",
                getWidth() / 2f, startY + lines.size() * dp(18) + dp(4), textPaint);
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

    private void drawChallengeBadges(Canvas canvas, float centerX, float centerY) {
        int count = (challengeCleared ? 1 : 0) + (comboChallengeCleared ? 1 : 0) + (scoreChallengeCleared ? 1 : 0)
                + (hiddenChallengeCleared ? 1 : 0);
        if (count <= 0) {
            return;
        }

        float startX = centerX - (count - 1) * dp(12);
        for (int i = 0; i < count; i++) {
            paint.setColor(Color.argb(210, 255, 236, 118));
            canvas.drawCircle(startX + i * dp(24), centerY, dp(6), paint);
        }
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
        final int brushes;
        final int portalProps;
        final int cleanses;
        final int freezes;
        final int magnets;
        final int clocks;
        final int starHammers;
        final int rockets;
        final int targetBrushes;
        final int shields;
        final int energyCores;
        final int chainBreakers;
        final int lightnings;
        final int meteors;
        final int tides;
        final int auroraOrbs;
        final int starfishPicks;
        final int moonTickets;
        final int fireworkCannons;
        final int starCompasses;
        final int bubbleWands;
        final int snowGlobes;
        final int starHarps;
        final int targetKind;
        final int targetAmount;
        final int iceCount;
        final int honeyCount;
        final int stoneCount;
        final int vineCount;
        final int giftCount;
        final int chainCount;
        final int shellCount;
        final int flowerCount;
        final int coralReefCount;
        final int keyCount;
        final int moveChestCount;
        final int cloudCount;
        final int gemCount;
        final int goldenEggCount;
        final int coinPouchCount;
        final int paintBucketCount;
        final int windmillCount;
        final int jewelBowCount;
        final int stardustJarCount;
        final int wishLampCount;
        final int resonanceDrumCount;
        final int auroraPrismCount;
        final int rainbowBottleCount;
        final int energyPotionCount;
        final int butterflyCount;
        final int portalCount;
        final int hourglassCount;
        final int luckyStarCount;
        final int luckyCloverCount;
        final int mysteryBoxCount;
        final int pearlCount;
        final int carouselCount;
        final int ferrisTicketCount;
        final int fireworksBarrelCount;
        final int starportBeaconCount;
        final int meteorTrailCount;
        final int rainbowArcCount;
        final int crystalCoreCount;
        final int musicBoxCount;
        final int countdownBombCount;
        final int moveLimitGoal;
        final int comboGoal;
        final int scoreGoal;
        final boolean elite;

        Level(int targetScore, int moves, int hammers, int bombs, int shuffles, int rowBlasts, int colorBlasts,
                int extraMoves, int magicWands, int brushes, int portalProps, int cleanses, int freezes,
                int magnets, int clocks, int starHammers, int rockets, int targetBrushes, int shields, int energyCores, int chainBreakers, int lightnings, int meteors, int tides, int auroraOrbs, int starfishPicks, int moonTickets, int fireworkCannons, int starCompasses, int bubbleWands, int snowGlobes, int starHarps, int targetKind, int targetAmount, int iceCount, int honeyCount, int stoneCount, int vineCount,
                int giftCount, int chainCount, int shellCount, int flowerCount, int coralReefCount, int keyCount, int moveChestCount,
                int cloudCount, int gemCount, int goldenEggCount, int coinPouchCount, int paintBucketCount, int windmillCount, int jewelBowCount, int stardustJarCount, int wishLampCount, int resonanceDrumCount, int auroraPrismCount, int rainbowBottleCount, int energyPotionCount, int butterflyCount,
                int portalCount, int hourglassCount, int luckyStarCount, int luckyCloverCount,
                int mysteryBoxCount, int pearlCount, int carouselCount, int ferrisTicketCount, int fireworksBarrelCount, int starportBeaconCount, int meteorTrailCount, int rainbowArcCount, int crystalCoreCount, int musicBoxCount, int countdownBombCount, int moveLimitGoal, int comboGoal, int scoreGoal, boolean elite) {
            this.targetScore = targetScore;
            this.moves = moves;
            this.hammers = hammers;
            this.bombs = bombs;
            this.shuffles = shuffles;
            this.rowBlasts = rowBlasts;
            this.colorBlasts = colorBlasts;
            this.extraMoves = extraMoves;
            this.magicWands = magicWands;
            this.brushes = brushes;
            this.portalProps = portalProps;
            this.cleanses = cleanses;
            this.freezes = freezes;
            this.magnets = magnets;
            this.clocks = clocks;
            this.starHammers = starHammers;
            this.rockets = rockets;
            this.targetBrushes = targetBrushes;
            this.shields = shields;
            this.energyCores = energyCores;
            this.chainBreakers = chainBreakers;
            this.lightnings = lightnings;
            this.meteors = meteors;
            this.tides = tides;
            this.auroraOrbs = auroraOrbs;
            this.starfishPicks = starfishPicks;
            this.moonTickets = moonTickets;
            this.fireworkCannons = fireworkCannons;
            this.starCompasses = starCompasses;
            this.bubbleWands = bubbleWands;
            this.snowGlobes = snowGlobes;
            this.starHarps = starHarps;
            this.targetKind = targetKind;
            this.targetAmount = targetAmount;
            this.iceCount = iceCount;
            this.honeyCount = honeyCount;
            this.stoneCount = stoneCount;
            this.vineCount = vineCount;
            this.giftCount = giftCount;
            this.chainCount = chainCount;
            this.shellCount = shellCount;
            this.flowerCount = flowerCount;
            this.coralReefCount = coralReefCount;
            this.keyCount = keyCount;
            this.moveChestCount = moveChestCount;
            this.cloudCount = cloudCount;
            this.gemCount = gemCount;
            this.goldenEggCount = goldenEggCount;
            this.coinPouchCount = coinPouchCount;
            this.paintBucketCount = paintBucketCount;
            this.windmillCount = windmillCount;
            this.jewelBowCount = jewelBowCount;
            this.stardustJarCount = stardustJarCount;
            this.wishLampCount = wishLampCount;
            this.resonanceDrumCount = resonanceDrumCount;
            this.auroraPrismCount = auroraPrismCount;
            this.rainbowBottleCount = rainbowBottleCount;
            this.energyPotionCount = energyPotionCount;
            this.butterflyCount = butterflyCount;
            this.portalCount = portalCount;
            this.hourglassCount = hourglassCount;
            this.luckyStarCount = luckyStarCount;
            this.luckyCloverCount = luckyCloverCount;
            this.mysteryBoxCount = mysteryBoxCount;
            this.pearlCount = pearlCount;
            this.carouselCount = carouselCount;
            this.ferrisTicketCount = ferrisTicketCount;
            this.fireworksBarrelCount = fireworksBarrelCount;
            this.starportBeaconCount = starportBeaconCount;
            this.meteorTrailCount = meteorTrailCount;
            this.rainbowArcCount = rainbowArcCount;
            this.crystalCoreCount = crystalCoreCount;
            this.musicBoxCount = musicBoxCount;
            this.countdownBombCount = countdownBombCount;
            this.moveLimitGoal = moveLimitGoal;
            this.comboGoal = comboGoal;
            this.scoreGoal = scoreGoal;
            this.elite = elite;
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
        final int score;

        Move(int rowA, int colA, int rowB, int colB) {
            this(rowA, colA, rowB, colB, 0);
        }

        Move(int rowA, int colA, int rowB, int colB, int score) {
            this.rowA = rowA;
            this.colA = colA;
            this.rowB = rowB;
            this.colB = colB;
            this.score = score;
        }
    }
}
