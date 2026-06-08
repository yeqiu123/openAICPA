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
    private static final String KEY_COINS = "coins";
    private static final String KEY_STAR_CHEST_CLAIMED = "star_chest_claimed";
    private static final String KEY_RANK_CHEST_CLAIMED = "rank_chest_claimed";
    private static final String KEY_CHAPTER_CHEST_PREFIX = "chapter_chest_";
    private static final String KEY_CHAPTER_MASTERY_PREFIX = "chapter_mastery_";
    private static final String KEY_CHAPTER_ELITE_PREFIX = "chapter_elite_";
    private static final String KEY_CHAPTER_RANK_PREFIX = "chapter_rank_";
    private static final String KEY_ACHIEVEMENT_PREFIX = "achievement_";
    private static final String KEY_WIN_STREAK = "win_streak";
    private static final String KEY_DAILY_REWARD_DAY = "daily_reward_day";
    private static final String KEY_DAILY_STREAK = "daily_streak";
    private static final String KEY_DAILY_CHALLENGE_DAY = "daily_challenge_day";
    private static final String KEY_DAILY_CHALLENGE_STREAK = "daily_challenge_streak";
    private static final String KEY_DAILY_GOAL_DAY = "daily_goal_day";
    private static final String KEY_DAILY_GOAL_PROGRESS = "daily_goal_progress";
    private static final String KEY_DAILY_GOAL_CLAIMED = "daily_goal_claimed";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_HAPTIC_ENABLED = "haptic_enabled";
    private static final int BOARD_SIZE = 8;
    private static final int TILE_KINDS = 6;
    private static final int LEVEL_COUNT = 180;
    private static final int LEVELS_PER_PAGE = 60;
    private static final int CONTINUE_COST = 10;
    private static final int STAR_CHEST_STEP = 30;
    private static final int RANK_CHEST_STEP = 45;
    private static final int CHAPTER_SIZE = 20;
    private static final int CHAPTER_COUNT = 9;
    private static final int CHAPTER_CHEST_STARS = 45;
    private static final int ACHIEVEMENT_COUNT = 18;
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
    private static final int PROP_COUNT = 25;
    private static final int[] PROP_COSTS = {8, 12, 10, 16, 18, 14, 22, 20, 24, 20, 18, 16, 18, 26, 18, 20, 22, 24, 20, 22, 24, 26, 28, 24, 26};

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
    private final RectF rankChestRect = new RectF();
    private final RectF dailyChallengeRect = new RectF();
    private final RectF dailyGoalRect = new RectF();
    private final RectF chapterChestRect = new RectF();
    private final RectF replayHintRect = new RectF();
    private final int[] levelStars = new int[LEVEL_COUNT];
    private final int[] levelBestScores = new int[LEVEL_COUNT];
    private final int[] levelRanks = new int[LEVEL_COUNT];
    private final boolean[] chapterChestClaimed = new boolean[CHAPTER_COUNT];
    private final boolean[] chapterMasteryClaimed = new boolean[CHAPTER_COUNT];
    private final boolean[] chapterEliteClaimed = new boolean[CHAPTER_COUNT];
    private final boolean[] chapterRankClaimed = new boolean[CHAPTER_COUNT];
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
            Color.rgb(110, 125, 255)
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
            Color.rgb(255, 174, 208)
    };
    private final String[] chapterNames = {
            "糖果森林", "云朵海湾", "果冻火山", "薄荷花园", "星光梦境", "蜂蜜工坊", "珊瑚集市", "极光城堡", "月光游乐园"
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
    private int winStreak;
    private int lastWinStreakReward;
    private int lastStarUpgradeReward;
    private int lastRankUpgradeReward;
    private int lastPerfectReward;
    private int lastHiddenReward;
    private int lastEliteReward;
    private int lastFirstClearReward;
    private int lastFullStarReward;
    private int starChestClaimed;
    private int rankChestClaimed;
    private int lastChestReward;
    private int lastRankChestReward;
    private int lastChapterChestReward;
    private int lastChapterMasteryReward;
    private int lastChapterEliteReward;
    private int lastChapterRankReward;
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
    private int lastEnergyRewardProp = NONE;
    private int lastChestNoticeType;
    private int dailyRewardAmount;
    private int dailyStreak;
    private int dailyChallengeStreak;
    private int dailyGoalProgress;
    private int lastDailyChallengeMilestoneProp = NONE;
    private int lastDailyChallengeMilestoneAmount;
    private int lastDailyGoalReward;
    private int rewardTargetMilestone;
    private int rewardObstacleMilestone;
    private int rewardComboMilestone;
    private int rewardKeyMilestone;
    private int lastShieldReward;
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
                usedContinueThisLevel = true;
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
            int countdownBombCount = i < 58 || i % 10 != 7 ? 0 : 1 + (i % 30 == 7 ? 1 : 0);
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
                    magicWand, brush, portalProp, cleanse, freeze, magnet, clock, starHammer, rocket, targetBrush, shield, energyCore, chainBreaker, lightning, meteor, tide, auroraOrb, starfishPick, moonTicket, targetKind, targetAmount, iceCount, honeyCount, stoneCount, vineCount, giftCount,
                    chainCount, shellCount, flowerCount, coralReefCount, keyCount, moveChestCount, cloudCount, gemCount, goldenEggCount, coinPouchCount, paintBucketCount, windmillCount, jewelBowCount, stardustJarCount, wishLampCount, resonanceDrumCount, auroraPrismCount, rainbowBottleCount, energyPotionCount, butterflyCount, portalCount, hourglassCount, luckyStarCount, luckyCloverCount, mysteryBoxCount, pearlCount, carouselCount, ferrisTicketCount, fireworksBarrelCount, countdownBombCount,
                    moveLimitGoal, comboGoal, scoreGoal, elite));
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
        comboFeverMoves = 0;
        honeyFreezeMoves = 0;
        bombShieldCount = 0;
        bestCombo = 0;
        lastRank = 0;
        lastCoinReward = 0;
        lastAchievementReward = 0;
        lastWinStreakReward = 0;
        lastStarUpgradeReward = 0;
        lastRankUpgradeReward = 0;
        lastPerfectReward = 0;
        lastHiddenReward = 0;
        lastEliteReward = 0;
        lastFirstClearReward = 0;
        lastFullStarReward = 0;
        lastDailyGoalReward = 0;
        lastDailyChallengeMilestoneProp = NONE;
        lastDailyChallengeMilestoneAmount = 0;
        lastChestReward = 0;
        lastRankChestReward = 0;
        lastChapterChestReward = 0;
        lastChapterMasteryReward = 0;
        lastChapterEliteReward = 0;
        lastChapterRankReward = 0;
        lastChestNoticeType = 0;
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
        applyChapterMasteryStarterPerks();

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
        placeCountdownBomb(level.countdownBombCount, Math.max(5, level.moves / 2));
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
                } else if (prop == PROP_PORTAL) {
                    // 传送道具即时扰动棋盘，适合主动寻找新连锁。
                    propInventory[prop]--;
                    triggerPortalShift();
                    resolveMatches(findMatches());
                    checkLevelState();
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
                } else if (prop == PROP_FREEZE) {
                    // 冻结道具暂停蜂蜜蔓延，给高压局面留出规划窗口。
                    propInventory[prop]--;
                    honeyFreezeMoves = 4;
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_MAGNET) {
                    // 磁铁直接吸走当前目标色，帮助玩家补齐收集目标。
                    propInventory[prop]--;
                    clearCells(buildColorCells(targetKind), 160);
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_CLOCK) {
                    // 时钟补步并延缓炸弹倒计时，专门应对后期高压关卡。
                    propInventory[prop]--;
                    movesLeft += 3;
                    moveLimitBonus += 3;
                    extendCountdownBombs(2);
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_SHIELD) {
                    // 护盾即时生效，能抵消一次倒计时炸弹归零。
                    propInventory[prop]--;
                    bombShieldCount++;
                    extendCountdownBombs(1);
                    lastShieldReward = bombShieldCount;
                    lastTaskRewardType = 10;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    showFeedback(1, bombShieldCount);
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_ENERGY_CORE) {
                    // 能量核心直接填满能量并补给随机道具，制造一次主动爆发机会。
                    propInventory[prop]--;
                    comboEnergy = 100;
                    lastEnergyRewardProp = random.nextInt(PROP_COUNT);
                    propInventory[lastEnergyRewardProp]++;
                    lastTaskRewardType = 11;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    showFeedback(1, 100);
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_METEOR) {
                    // 流星随机砸开多处棋盘格，适合临近失败时制造翻盘机会。
                    propInventory[prop]--;
                    clearCells(buildRandomCells(8), 240);
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_TIDE) {
                    // 潮汐横扫多行棋盘，适合后期大面积打开局面。
                    propInventory[prop]--;
                    clearCells(buildTideCells(), 260);
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_AURORA_ORB) {
                    // 极光球补满能量并生成彩虹棋，适合极光章节制造爆发。
                    propInventory[prop]--;
                    comboEnergy = 100;
                    upgradeRandomRainbowPiece();
                    lastTaskRewardType = 13;
                    lastFerrisTicketReward = 0;
                    lastFireworksBarrelReward = 0;
                    showFeedback(1, 100);
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_STARFISH_PICK) {
                    // 海星镐随机敲开多层障碍，专门应对后期珊瑚礁和贝壳压力。
                    propInventory[prop]--;
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
                    showFeedback(1, Math.max(1, chipped));
                    checkLevelState();
                    activeProp = NONE;
                    selectedRow = NONE;
                    selectedCol = NONE;
                } else if (prop == PROP_MOON_TICKET) {
                    // 月光票券补步并送一枚方向特效，作为终章关卡的轻量翻盘道具。
                    propInventory[prop]--;
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
                    showFeedback(1, 2);
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
        } else if (activeProp == PROP_ROCKET) {
            // 火箭按点击格的奇偶方向清一行或一列，适合精确打开局面。
            propInventory[PROP_ROCKET]--;
            clearCells(buildRocketCells(row, col), 190);
        } else if (activeProp == PROP_LIGHTNING) {
            // 闪电沿两条对角线劈开棋盘，适合打开被斜向隔断的局面。
            propInventory[PROP_LIGHTNING]--;
            clearCells(buildDiagonalCells(row, col), 210);
        } else if (activeProp == PROP_TARGET_BRUSH) {
            // 目标刷把小范围棋子染成目标色，帮助收集关续上消除机会。
            propInventory[PROP_TARGET_BRUSH]--;
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
            lastEnergyRewardProp = NONE;
            showFeedback(1, Math.max(1, painted));
        } else if (activeProp == PROP_MAGIC_WAND) {
            // 魔法棒把指定棋子升级成彩虹棋，让玩家主动创造关键大招。
            propInventory[PROP_MAGIC_WAND]--;
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
            lastEnergyRewardProp = NONE;
            showFeedback(1, 1);
        } else if (activeProp == PROP_BRUSH) {
            // 克隆刷把普通棋升级成方向特效，方便玩家主动铺垫连锁。
            propInventory[PROP_BRUSH]--;
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
            lastEnergyRewardProp = NONE;
            showFeedback(1, 1);
        } else if (activeProp == PROP_STAR_HAMMER) {
            // 星锤把指定棋子锤成爆炸特效，用来主动制造更大的连锁。
            propInventory[PROP_STAR_HAMMER]--;
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
            lastEnergyRewardProp = NONE;
            showFeedback(1, 1);
        } else if (activeProp == PROP_CLEANSE) {
            // 净化道具直接削弱周围障碍，适合处理蜂蜜和多层阻挡。
            propInventory[PROP_CLEANSE]--;
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
            lastEnergyRewardProp = NONE;
            showFeedback(1, Math.max(1, cleaned));
        } else if (activeProp == PROP_CHAIN_BREAKER) {
            // 破锁钳专门剪开锁链和藤蔓，帮助玩家快速打开被封住的区域。
            propInventory[PROP_CHAIN_BREAKER]--;
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
            clearHint();
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
            clearHint();
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
        while (!matches.isEmpty()) {
            combo++;
            matches = expandSpecialCells(matches);
            totalCleared += matches.size();
            score += applyComboFeverScore(matches.size() * 60 + (combo - 1) * 120);
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
                comboFeverMoves = 3;
                lastEnergyRewardProp = random.nextInt(PROP_COUNT);
                propInventory[lastEnergyRewardProp]++;
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

    private void grantPerfectClearReward(Level level) {
        if (dailyChallengeMode || usedContinueThisLevel || movesUsed > Math.max(6, level.moves / 2) || lastRank < 5) {
            return;
        }

        // 完美通关奖励少步数、高评级的打法，给重玩提供更明确的冲刺目标。
        lastPerfectReward = 18 + lastRank * 4;
        coins += lastPerfectReward;
        propInventory[PROP_ROW_BLAST]++;
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
        if (dailyChallengeStreak == 3) {
            lastDailyChallengeMilestoneProp = PROP_ROCKET;
            lastDailyChallengeMilestoneAmount = 1;
        } else if (dailyChallengeStreak == 7) {
            lastDailyChallengeMilestoneProp = PROP_AURORA_ORB;
            lastDailyChallengeMilestoneAmount = 1;
        } else if (dailyChallengeStreak == 14) {
            lastDailyChallengeMilestoneProp = PROP_MOON_TICKET;
            lastDailyChallengeMilestoneAmount = 2;
        } else if (dailyChallengeStreak > 0 && dailyChallengeStreak % 30 == 0) {
            lastDailyChallengeMilestoneProp = PROP_METEOR;
            lastDailyChallengeMilestoneAmount = 2;
        }
        if (lastDailyChallengeMilestoneProp != NONE) {
            // 每日挑战连胜节点给稀有道具，强化持续回访动力。
            propInventory[lastDailyChallengeMilestoneProp] += lastDailyChallengeMilestoneAmount;
        }
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
            lastChapterChestReward = 0;
            lastDailyGoalReward = 0;
            lastChestNoticeType = 4;
            chestNoticeUntilTime = System.currentTimeMillis() + 1400;
            return;
        }

        lastDailyGoalReward = 35 + Math.min(15, dailyStreak * 2);
        lastChestReward = 0;
        lastRankChestReward = 0;
        lastChapterChestReward = 0;
        coins += lastDailyGoalReward;
        propInventory[PROP_MOON_TICKET]++;
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
        checkAchievement(6, getTotalRankScore() >= 180, 150);
        checkAchievement(7, lastRank >= 6, 120);
        checkAchievement(8, hiddenChallengeCleared, 100);
        checkAchievement(9, lastGemReward > 0 || lastCoinPouchReward > 0 || lastWishLampReward > 0
                || lastResonanceDrumReward > 0 || lastAuroraPrismReward > 0 || lastPortalReward > 0
                || lastLuckyStarRewardProp != NONE || lastLuckyCloverRewardType > 0 || lastMysteryRewardType > 0, 90);
        checkAchievement(10, getTotalStars() >= 300, 220);
        checkAchievement(11, getTotalRankScore() >= 420, 240);
        checkAchievement(12, getFullyClearedChapterCount() >= 3, 260);
        checkAchievement(13, getTotalClearedEliteCount() >= 8, 220);
        // 后期成就继续覆盖终章、总星数和高评级，给满级玩家更长的追求线。
        checkAchievement(14, getChapterStars(CHAPTER_COUNT - 1) >= 45, 280);
        checkAchievement(15, getTotalStars() >= 420, 320);
        checkAchievement(16, getTotalRankScore() >= 700, 360);
        checkAchievement(17, getFullyClearedChapterCount() >= CHAPTER_COUNT, 420);
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
        rankChestClaimed = prefs.getInt(KEY_RANK_CHEST_CLAIMED, 0);
        winStreak = prefs.getInt(KEY_WIN_STREAK, 0);
        dailyChallengeStreak = prefs.getInt(KEY_DAILY_CHALLENGE_STREAK, 0);
        soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true);
        hapticEnabled = prefs.getBoolean(KEY_HAPTIC_ENABLED, true);
        grantDailyReward();
        loadDailyGoal();
        for (int i = 0; i < levels.size(); i++) {
            levelStars[i] = prefs.getInt(KEY_STARS_PREFIX + i, 0);
            levelBestScores[i] = prefs.getInt(KEY_BEST_SCORE_PREFIX + i, 0);
            levelRanks[i] = prefs.getInt(KEY_RANK_PREFIX + i, 0);
        }
        for (int i = 0; i < chapterChestClaimed.length; i++) {
            chapterChestClaimed[i] = prefs.getBoolean(KEY_CHAPTER_CHEST_PREFIX + i, false);
            chapterMasteryClaimed[i] = prefs.getBoolean(KEY_CHAPTER_MASTERY_PREFIX + i, false);
            chapterEliteClaimed[i] = prefs.getBoolean(KEY_CHAPTER_ELITE_PREFIX + i, false);
            chapterRankClaimed[i] = prefs.getBoolean(KEY_CHAPTER_RANK_PREFIX + i, false);
        }
        for (int i = 0; i < achievementsClaimed.length; i++) {
            achievementsClaimed[i] = prefs.getBoolean(KEY_ACHIEVEMENT_PREFIX + i, false);
        }
    }

    private void saveLevelProgress() {
        Level level = levels.get(levelIndex);
        int oldStars = levelStars[levelIndex];
        int oldRank = levelRanks[levelIndex];
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
        }
        if (hiddenChallengeCleared) {
            lastHiddenReward = 20;
            coins += lastHiddenReward;
            propInventory[PROP_BOMB]++;
        }
        grantFirstClearReward(level, oldStars);
        grantEliteLevelReward(level);
        grantPerfectClearReward(level);
        grantAchievementRewards();
        grantChapterEliteReward();
        grantChapterRankReward();
        grantChapterMasteryReward();
        updateDailyGoalProgress();
        prefs.edit()
                .putInt(KEY_UNLOCKED_LEVEL, highestUnlockedLevel)
                .putInt(KEY_STARS_PREFIX + levelIndex, levelStars[levelIndex])
                .putInt(KEY_BEST_SCORE_PREFIX + levelIndex, levelBestScores[levelIndex])
                .putInt(KEY_RANK_PREFIX + levelIndex, levelRanks[levelIndex])
                .putInt(KEY_COINS, coins)
                .putInt(KEY_STAR_CHEST_CLAIMED, starChestClaimed)
                .putInt(KEY_RANK_CHEST_CLAIMED, rankChestClaimed)
                .apply();
    }

    private void grantFullStarReward(Level level) {
        // 首次满星额外给奖励，把补星目标转成更明确的正反馈。
        lastFullStarReward = 24 + getChapterIndex(levelIndex) * 4 + (level.elite ? 12 : 0);
        coins += lastFullStarReward;
        propInventory[PROP_CLEANSE]++;
    }

    private void grantFirstClearReward(Level level, int oldStars) {
        if (oldStars > 0) {
            return;
        }

        // 首次通关奖励强化主线推进感，和重玩补星奖励区分开。
        lastFirstClearReward = 18 + Math.min(60, levelIndex / 2) + (level.elite ? 18 : 0);
        coins += lastFirstClearReward;
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
        propInventory[PROP_METEOR]++;
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
        propInventory[PROP_TIDE]++;
        prefs.edit()
                .putBoolean(KEY_CHAPTER_RANK_PREFIX + chapter, true)
                .putInt(KEY_COINS, coins)
                .apply();
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
        propInventory[PROP_MAGIC_WAND]++;
        if (mastered >= 2) {
            propInventory[PROP_BRUSH]++;
        }
        if (mastered >= 4) {
            propInventory[PROP_COLOR_BLAST]++;
        }
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
            focusReplayLevel();
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

    private void claimChapterChest() {
        int chapter = getCurrentMapChapter();
        if (!canClaimChapterChest(chapter)) {
            lastChapterChestReward = 0;
            lastChestReward = 0;
            lastRankChestReward = 0;
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
        lastDailyGoalReward = 0;
        lastChestNoticeType = 3;
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
                    && ferrisTicket[row][col] == 0 && fireworksBarrel[row][col] == 0) {
                // 倒计时炸弹必须在归零前清掉，给后期关卡制造明确压力。
                countdownBomb[row][col] = timer + random.nextInt(3);
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
                propInventory[lastLuckyStarRewardProp]++;
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
                propInventory[PROP_STARFISH_PICK]++;
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
                propInventory[PROP_MOON_TICKET]++;
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
            if (countdownBomb[cell.row][cell.col] > 0) {
                countdownBomb[cell.row][cell.col] = 0;
                score += 180;
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
            lastEnergyRewardProp = NONE;
            saveCoins();
        } else {
            propInventory[random.nextInt(PROP_COUNT)]++;
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
            propInventory[lastLuckyCloverRewardProp]++;
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
            propInventory[lastMysteryRewardProp]++;
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
            propInventory[PROP_HAMMER] += targetMilestone - rewardTargetMilestone;
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
            lastEnergyRewardProp = NONE;
            showFeedback(1, 5);
        }

        int clearedObstacles = level.iceCount + level.honeyCount + level.stoneCount + level.vineCount + level.chainCount
                + level.shellCount + level.coralReefCount + level.flowerCount - iceRemaining - honeyRemaining - stoneRemaining - vineRemaining
                - chainRemaining - shellRemaining - coralReefRemaining - flowerRemaining;
        int obstacleMilestone = clearedObstacles / 6;
        if (obstacleMilestone > rewardObstacleMilestone) {
            // 清障越积极，道具补给越快。
            propInventory[PROP_BOMB] += obstacleMilestone - rewardObstacleMilestone;
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
            lastEnergyRewardProp = NONE;
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
            lastEnergyRewardProp = NONE;
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
            lastEnergyRewardProp = NONE;
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
        } else {
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
        if (level.coralReefCount > 0) {
            obstacleText += " 礁" + coralReefRemaining;
        }
        if (level.flowerCount > 0) {
            obstacleText += " 花" + flowerRemaining;
        }
        if (level.keyCount > 0) {
            obstacleText += " 钥" + keyRemaining;
        }
        canvas.drawText(obstacleText, getWidth() - dp(22), dp(130), textPaint);
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
            starText += " 隐" + movesUsed + "/" + Math.max(7, level.moves - 4);
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
        paint.setStyle(Paint.Style.FILL);
        postInvalidateOnAnimation();
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
        float startY = dp(212);
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
        }

        drawLevelMapPager(canvas);
    }

    private void drawLevelTypeMark(Canvas canvas, int levelIndex, RectF rect) {
        String mark = buildLevelTypeMark(levelIndex);
        if (mark.length() == 0) {
            return;
        }

        paint.setColor(Color.argb(190, 33, 37, 56));
        RectF badge = new RectF(rect.left + dp(4), rect.bottom - dp(18), rect.left + dp(22), rect.bottom - dp(4));
        canvas.drawRoundRect(badge, dp(5), dp(5), paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(8));
        textPaint.setColor(Color.WHITE);
        canvas.drawText(mark, badge.centerX(), badge.centerY() + dp(3), textPaint);
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
            canvas.drawText(chapterNames[chapter] + " " + getChapterStars(chapter),
                    rect.centerX(), rect.centerY() + dp(4), textPaint);
        }
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

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(12));
        textPaint.setColor(claimed ? Color.WHITE : Color.rgb(33, 37, 56));
        String text = claimed ? "每日挑战 再玩" : "每日挑战 奖励";
        if (dailyChallengeStreak > 1) {
            text += " 连" + dailyChallengeStreak;
        }
        canvas.drawText(text, dailyChallengeRect.centerX(), dailyChallengeRect.centerY() + dp(5), textPaint);
        drawDailyGoalEntry(canvas);
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

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(12));
        textPaint.setColor(claimable ? Color.rgb(33, 37, 56) : Color.WHITE);
        String text = dailyGoalClaimed ? "每日目标 已领"
                : "每日目标 " + Math.min(6, dailyGoalProgress) + "/6星";
        if (claimable) {
            text += " 月票";
        }
        canvas.drawText(text, dailyGoalRect.centerX(), dailyGoalRect.centerY() + dp(5), textPaint);
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
        String status = getChapterStars(chapter) >= CHAPTER_SIZE * 3
                ? (chapterMasteryClaimed[chapter] ? "  大师已领" : "  大师奖励") : "";
        String eliteStatus = getChapterEliteCount(chapter) > 0
                && getChapterClearedEliteCount(chapter) >= getChapterEliteCount(chapter)
                ? (chapterEliteClaimed[chapter] ? " 已领" : " 奖励") : "";
        String rankStatus = getChapterRankScore(chapter) >= getChapterRankRewardTarget()
                ? (chapterRankClaimed[chapter] ? " 已领" : " 奖励") : "";
        canvas.drawText("章节进度 " + getChapterUnlockedCount(chapter) + "/" + CHAPTER_SIZE
                + "  星 " + getChapterStars(chapter) + status, getWidth() / 2f, top + dp(26), textPaint);
        canvas.drawText("章节评级 " + getChapterRankScore(chapter) + "/" + getChapterRankRewardTarget() + rankStatus
                        + "  精英 " + getChapterClearedEliteCount(chapter) + "/" + getChapterEliteCount(chapter) + eliteStatus,
                getWidth() / 2f, top + dp(40), textPaint);
    }

    private void drawAchievementProgress(Canvas canvas) {
        float left = dp(34);
        float top = dp(194);
        float right = getWidth() - dp(34);
        float progress = getClaimedAchievementCount() / (float) ACHIEVEMENT_COUNT;

        paint.setColor(Color.argb(80, 33, 37, 56));
        canvas.drawRoundRect(new RectF(left, top, right, top + dp(8)), dp(4), dp(4), paint);
        paint.setColor(Color.argb(210, 116, 219, 214));
        canvas.drawRoundRect(new RectF(left, top, left + (right - left) * progress, top + dp(8)), dp(4), dp(4), paint);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(11));
        textPaint.setColor(Color.WHITE);
        canvas.drawText("成就 " + getClaimedAchievementCount() + "/" + ACHIEVEMENT_COUNT
                + "  评级 " + getTotalRankScore(), getWidth() / 2f, top + dp(22), textPaint);
    }

    private void drawReplayHintEntry(Canvas canvas) {
        replayHintRect.set(dp(28), dp(186), getWidth() - dp(28), dp(210));
        int replayLevel = findReplayTargetLevel();
        paint.setColor(replayLevel >= 0 ? Color.argb(120, 255, 236, 133) : Color.argb(70, 255, 255, 255));
        canvas.drawRoundRect(replayHintRect, dp(10), dp(10), paint);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(11));
        textPaint.setColor(Color.WHITE);
        String text = replayLevel >= 0 ? "补星推荐 第" + (replayLevel + 1) + "关  点击定位" : "已通关卡暂无补星目标";
        canvas.drawText(text, replayHintRect.centerX(), replayHintRect.centerY() + dp(4), textPaint);
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
        canvas.drawText(buildStarChestLabel(), starChestRect.centerX(), starChestRect.centerY() + dp(5), textPaint);
        canvas.drawText(buildRankChestLabel(), rankChestRect.centerX(), rankChestRect.centerY() + dp(5), textPaint);
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

    private String buildRankChestLabel() {
        if (getAvailableRankChests() > 0) {
            return "评级+" + (40 + (rankChestClaimed + 1) * 8);
        }
        return "评 " + getTotalRankScore() + "/" + getNextRankChestTarget();
    }

    private void drawStarChestNotice(Canvas canvas, float pagerTop) {
        if (System.currentTimeMillis() > chestNoticeUntilTime) {
            return;
        }

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(sp(13));
        textPaint.setColor(Color.WHITE);
        String text = lastChestReward > 0 ? "星级宝箱 金币+" + lastChestReward
                : buildChestNoticeFallback();
        if (lastChapterChestReward > 0) {
            text = "章节宝箱 金币+" + lastChapterChestReward;
        } else if (lastRankChestReward > 0) {
            text = "评级宝箱 金币+" + lastRankChestReward;
        } else if (lastDailyGoalReward > 0) {
            text = "每日目标 金币+" + lastDailyGoalReward + " 月票+1";
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

    private int getClaimedAchievementCount() {
        int count = 0;
        for (boolean claimed : achievementsClaimed) {
            if (claimed) {
                count++;
            }
        }
        return count;
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

    private int findReplayTargetLevel() {
        for (int level = 0; level <= highestUnlockedLevel && level < levels.size(); level++) {
            if (isReplayTargetLevel(level)) {
                return level;
            }
        }
        return NONE;
    }

    private boolean isReplayTargetLevel(int level) {
        return level <= highestUnlockedLevel && levelStars[level] > 0
                && (levelStars[level] < 3 || levelRanks[level] < 4);
    }

    private void focusReplayLevel() {
        int level = findReplayTargetLevel();
        if (level < 0) {
            return;
        }

        // 补星推荐直接定位到最早可提升关卡，减少地图翻找成本。
        levelMapPage = level / LEVELS_PER_PAGE;
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
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(4));
            paint.setColor(Color.rgb(255, 244, 170));
            canvas.drawRoundRect(rect, dp(14), dp(14), paint);
            paint.setStyle(Paint.Style.FILL);
            postInvalidateOnAnimation();
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

            paint.setColor(activeProp == prop ? Color.argb(235, 255, 255, 255) : Color.argb(120, 255, 255, 255));
            canvas.drawRoundRect(rect, dp(12), dp(12), paint);

            paint.setColor(Color.argb(propInventory[prop] > 0 ? 220 : 85, 33, 37, 56));
            drawPropIcon(canvas, prop, rect.centerX(), rect.centerY() - dp(6));

            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(sp(9));
            textPaint.setColor(Color.WHITE);
            String label = propInventory[prop] > 0
                    ? getPropName(prop) + " x" + propInventory[prop]
                    : getPropName(prop) + " " + PROP_COSTS[prop] + "币";
            canvas.drawText(label, rect.centerX(), rect.bottom - dp(7), textPaint);
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

    private void drawCountdownBomb(Canvas canvas, int row, int col, RectF rect) {
        if (countdownBomb[row][col] <= 0) {
            return;
        }

        float centerX = rect.left + rect.width() * 0.24f;
        float centerY = rect.bottom - dp(18);
        int timer = countdownBomb[row][col];
        paint.setColor(timer <= 2 ? Color.argb(230, 255, 88, 112) : Color.argb(220, 33, 37, 56));
        canvas.drawCircle(centerX, centerY, dp(13), paint);
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
        if (level.countdownBombCount > 0) {
            goalText += "  炸弹 " + level.countdownBombCount;
        }
        if (level.elite) {
            goalText += "  精英奖励";
        }
        if (level.scoreGoal > 0) {
            goalText += "  高分 " + level.scoreGoal;
        }
        if (isHiddenChallengeLevel()) {
            goalText += "  隐藏步限 " + Math.max(7, level.moves - 4);
        }
        canvas.drawText(goalText,
                getWidth() / 2f, centerY + dp(32), textPaint);
        postInvalidateOnAnimation();
    }

    private int getLevelObstacleCount(Level level) {
        return level.iceCount + level.honeyCount + level.stoneCount + level.vineCount + level.chainCount
                + level.shellCount + level.coralReefCount + level.flowerCount;
    }

    private void showFeedback(int combo, int cleared) {
        feedbackCombo = combo;
        feedbackCleared = cleared;
        feedbackStartTime = System.currentTimeMillis();
    }

    private void showNormalFeedback(int combo, int cleared) {
        lastTaskRewardType = 0;
        lastEnergyRewardProp = NONE;
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
                || lastPerfectReward > 0 || lastHiddenReward > 0 || lastWinStreakReward > 0
                || lastEliteReward > 0 || lastFirstClearReward > 0 || lastFullStarReward > 0
                || lastChapterMasteryReward > 0 || lastChapterEliteReward > 0 || lastChapterRankReward > 0)) {
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
            if (lastFullStarReward > 0) {
                bonusText += "  满星达成";
            }
            if (lastPerfectReward > 0) {
                bonusText += "  完美通关";
            }
            if (lastChapterEliteReward > 0) {
                bonusText += "  章节精英";
            }
            if (lastChapterRankReward > 0) {
                bonusText += "  章节评级";
            }
            canvas.drawText(bonusText, getWidth() / 2f, getHeight() * 0.49f, textPaint);
            drawChallengeBadges(canvas, getWidth() / 2f, getHeight() * 0.515f);
            String scoreText = dailyChallengeMode ? "挑战分 " + score : "最佳分 " + levelBestScores[levelIndex];
            canvas.drawText(scoreText, getWidth() / 2f, getHeight() * 0.55f, textPaint);
            String rewardText = "金币 +" + lastCoinReward + "  点击继续";
            if (dailyChallengeMode) {
                rewardText = lastCoinReward > 0 ? "每日金币 +" + lastCoinReward + " 连" + dailyChallengeStreak + "  返回主线"
                        : "今日已领奖  返回主线";
                if (lastDailyChallengeMilestoneProp != NONE) {
                    rewardText = "每日金币 +" + lastCoinReward + " 连" + dailyChallengeStreak + " "
                            + getPropName(lastDailyChallengeMilestoneProp) + "+" + lastDailyChallengeMilestoneAmount + "  返回主线";
                }
            } else if (lastChapterMasteryReward > 0) {
                rewardText = "金币 +" + lastCoinReward + " 满星大师+" + lastChapterMasteryReward + "  点击继续";
            } else if (lastChapterEliteReward > 0) {
                rewardText = "金币 +" + lastCoinReward + " 章节精英+" + lastChapterEliteReward + " 流星+1  点击继续";
            } else if (lastChapterRankReward > 0) {
                rewardText = "金币 +" + lastCoinReward + " 章节评级+" + lastChapterRankReward + " 潮汐+1  点击继续";
            } else if (lastAchievementReward > 0) {
                rewardText = "金币 +" + lastCoinReward + "  成就奖励+" + lastAchievementReward + "  点击继续";
            } else if (lastFirstClearReward > 0) {
                rewardText = "金币 +" + lastCoinReward + "  首通+" + lastFirstClearReward + "  点击继续";
            } else if (lastFullStarReward > 0) {
                rewardText = "金币 +" + lastCoinReward + "  满星+" + lastFullStarReward + " 净化+1  点击继续";
            } else if (lastStarUpgradeReward > 0) {
                rewardText = "金币 +" + lastCoinReward + "  补星+" + lastStarUpgradeReward + "  点击继续";
            } else if (lastEliteReward > 0) {
                rewardText = "金币 +" + lastCoinReward + "  精英+" + lastEliteReward + "  点击继续";
            } else if (lastPerfectReward > 0) {
                rewardText = "金币 +" + lastCoinReward + "  完美+" + lastPerfectReward + "  点击继续";
            } else if (lastHiddenReward > 0) {
                rewardText = "金币 +" + lastCoinReward + "  隐藏+" + lastHiddenReward + "  点击继续";
            } else if (lastRankUpgradeReward > 0) {
                rewardText = "金币 +" + lastCoinReward + "  评级+" + lastRankUpgradeReward + "  点击继续";
            } else if (lastWinStreakReward > 0) {
                rewardText = "金币 +" + lastCoinReward + " 连胜+" + lastWinStreakReward + "  点击继续";
            }
            canvas.drawText(rewardText, getWidth() / 2f, getHeight() * 0.61f, textPaint);
        } else if (countdownBombExploded) {
            canvas.drawText("炸弹爆炸，点击重试", getWidth() / 2f, getHeight() * 0.49f, textPaint);
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
        final int countdownBombCount;
        final int moveLimitGoal;
        final int comboGoal;
        final int scoreGoal;
        final boolean elite;

        Level(int targetScore, int moves, int hammers, int bombs, int shuffles, int rowBlasts, int colorBlasts,
                int extraMoves, int magicWands, int brushes, int portalProps, int cleanses, int freezes,
                int magnets, int clocks, int starHammers, int rockets, int targetBrushes, int shields, int energyCores, int chainBreakers, int lightnings, int meteors, int tides, int auroraOrbs, int starfishPicks, int moonTickets, int targetKind, int targetAmount, int iceCount, int honeyCount, int stoneCount, int vineCount,
                int giftCount, int chainCount, int shellCount, int flowerCount, int coralReefCount, int keyCount, int moveChestCount,
                int cloudCount, int gemCount, int goldenEggCount, int coinPouchCount, int paintBucketCount, int windmillCount, int jewelBowCount, int stardustJarCount, int wishLampCount, int resonanceDrumCount, int auroraPrismCount, int rainbowBottleCount, int energyPotionCount, int butterflyCount,
                int portalCount, int hourglassCount, int luckyStarCount, int luckyCloverCount,
                int mysteryBoxCount, int pearlCount, int carouselCount, int ferrisTicketCount, int fireworksBarrelCount, int countdownBombCount, int moveLimitGoal, int comboGoal, int scoreGoal, boolean elite) {
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

        Move(int rowA, int colA, int rowB, int colB) {
            this.rowA = rowA;
            this.colA = colA;
            this.rowB = rowB;
            this.colB = colB;
        }
    }
}
