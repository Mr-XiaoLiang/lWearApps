-----------------------------------------------------------------------------------------
--
-- main.lua
--
-----------------------------------------------------------------------------------------

local gameStatus = 0

local yLand = display.actualContentHeight - display.actualContentHeight * 0.2
local hLand = display.actualContentHeight * 0.1
local xLand = display.contentCenterX

local yBird = display.contentCenterY - 50
local xBird = display.contentCenterX - 50

local wPipe = display.contentCenterX + 10
local yReady = display.contentCenterY - 140

local uBird = -200
local vBird = 0
local wBird = -320
local g = 800
local dt = 0.025

local score = 0
local bestScore = 0
local scoreStep = 5

local bird
local land
local title
local getReady
local gameOver
local emitter

local board
local scoreTitle
local bestTitle
local silver
local gold

local soundEnable = false

local pipes = {}

local function playSound(s) 
    if (soundEnable) then
        audio.play(s)
    end
end

local function loadSounds()
    dieSound = audio.loadSound("Sounds/sfx_die.ogg")
    hitSound = audio.loadSound("Sounds/sfx_hit.ogg")
    pointSound = audio.loadSound("Sounds/sfx_point.ogg")
    swooshingSound = audio.loadSound("Sounds/sfx_swooshing.ogg")
    wingSound = audio.loadSound("Sounds/sfx_wing.ogg")
    boomSound = audio.loadSound("Sounds/sfx_boom.mp3")
end

local function calcRandomHole()
    return 100 + 20 * math.random(10)
end

local function loadBestScore()
    local path = system.pathForFile("bestscore.txt", system.DocumentsDirectory)

    -- Open the file handle
    local file, errorString = io.open(path, "r")

    if not file then
        -- Error occurred; output the cause
        print("File error: " .. errorString)
    else
        -- Read data from file
        local contents = file:read("*a")
        -- Output the file contents
        bestScore = tonumber(contents)
        -- Close the file handle
        io.close(file)
    end

    file = nil
end

local function saveBestScore()
    -- Path for the file to write
    local path = system.pathForFile("bestscore.txt", system.DocumentsDirectory)
    local file, errorString = io.open(path, "w")
    if not file then
        -- Error occurred; output the cause
        print("File error: " .. errorString)
    else
        file:write(bestScore)
        io.close(file)
    end
    file = nil


    -- show appodeal ad
    --  appodeal.show()
end


local function setupBird()
    local options =
    {
        width = 70,
        height = 50,
        numFrames = 4,
        sheetContentWidth = 280, -- width of original 1x size of entire sheet
        sheetContentHeight = 50 -- height of original 1x size of entire sheet
    }
    local imageSheet = graphics.newImageSheet("Assets/bird.png", options)

    local sequenceData =
    {
        name = "walking",
        start = 1,
        count = 3,
        time = 300,
        loopCount = 2,          -- Optional ; default is 0 (loop indefinitely)
        loopDirection = "forward" -- Optional ; values include "forward" or "bounce"
    }
    bird = display.newSprite(imageSheet, sequenceData)
    bird.x = xBird
    bird.y = yBird
end

local function prompt(tempo)
    bird:play()
end


local function initGame()
    score = 0
    scoreStep = 5
    title.text = score
    --  title.text = hLand

    for i = 1, 3 do
        pipes[i].x = 400 + display.contentCenterX * (i - 1)
        pipes[i].y = calcRandomHole()
    end
    yBird = display.contentCenterY - 50
    xBird = display.contentCenterX - 50
    getReady.y = 0
    getReady.alpha = 1
    gameOver.y = 0
    gameOver.alpha = 0
    board.y = 0
    board.alpha = 0
    playSound(swooshingSound)
    transition.to(bird, { time = 300, x = xBird, y = yBird, rotation = 0 })
    transition.to(getReady, { time = 600, y = yReady, transition = easing.outBounce, onComplete = prompt })
end


local function wing()
    if gameStatus == 0 then
        gameStatus = 1
        getReady.alpha = 0
    end

    if gameStatus == 1 then
        vBird = wBird
        bird:play()
        playSound(wingSound)
    end

    if gameStatus == 3 then
        gameStatus = 0
        initGame()
    end
end

local function setupExplosion()
    local dx = 31
    local p = "Assets/habra.png"
    local emitterParams = {
        startParticleSizeVariance = dx / 2,
        startColorAlpha = 0.61,
        startColorGreen = 0.3031555,
        startColorRed = 0.08373094,
        yCoordFlipped = 0,
        blendFuncSource = 770,
        blendFuncDestination = 1,
        rotatePerSecondVariance = 153.95,
        particleLifespan = 0.7237,
        tangentialAcceleration = -144.74,
        startParticleSize = dx,
        textureFileName = p,
        startColorVarianceAlpha = 1,
        maxParticles = 128,
        finishParticleSize = dx / 3,
        duration = 0.75,
        finishColorRed = 0.078,
        finishColorAlpha = 0.75,
        finishColorBlue = 0.3699196,
        finishColorGreen = 0.5443883,
        maxRadiusVariance = 172.63,
        finishParticleSizeVariance = dx / 2,
        gravityy = 220.0,
        speedVariance = 258.79,
        tangentialAccelVariance = -92.11,
        angleVariance = -300.0,
        angle = -900.11
    }
    emitter = display.newEmitter(emitterParams)
    emitter:stop()
end


local function explosion()
    emitter.x = bird.x
    emitter.y = bird.y
    emitter:start()
end




local function crash()
    gameStatus = 3
    playSound(hitSound)
    gameOver.y = 0
    gameOver.alpha = 1
    transition.to(gameOver, { time = 600, y = yReady, transition = easing.outBounce })
    board.y = 0
    board.alpha = 1

    if score > bestScore then
        bestScore = score
        saveBestScore()
    end
    bestTitle.text = bestScore
    scoreTitle.text = score
    if score < 10 then
        silver.alpha = 0
        gold.alpha = 0
    elseif score < 50 then
        silver.alpha = 1
        gold.alpha = 0
    else
        silver.alpha = 0
        gold.alpha = 1
    end
    transition.to(board, { time = 600, y = yReady + 100, transition = easing.outBounce })
end

local function collision(i)
    local dx = 40 -- horizontal space of hole
    local dy = 50 -- vertical space of hole
    local boom = 0
    local x = pipes[i].x
    local y = pipes[i].y

    if xBird > (x - dx) and xBird < (x + dx) then
        if yBird > (y + dy) or yBird < (y - dy) then
            boom = 1
        end
    end
    return boom
end

local function gameLoop()
    local eps = 10
    local leftEdge = -60
    if gameStatus == 1 then
        xLand = xLand + dt * uBird
        if xLand < 0 then
            xLand = display.contentCenterX * 2 + xLand
        end
        land.x = xLand
        for i = 1, 3 do
            local xb = xBird - eps
            local xOld = pipes[i].x
            local x = xOld + dt * uBird
            if x < leftEdge then
                x = wPipe * 3 + x
                pipes[i].y = calcRandomHole()
            end
            if xOld > xb and x <= xb then
                score = score + 1
                title.text = score
                if score == scoreStep then
                    scoreStep = scoreStep + 5
                    playSound(pointSound)
                end
            end
            pipes[i].x = x
            if collision(i) == 1 then
                explosion()
                playSound(dieSound)
                gameStatus = 2
            end
        end
    end

    if gameStatus == 1 or gameStatus == 2 then
        vBird = vBird + dt * g
        yBird = yBird + dt * vBird
        if yBird > yLand - eps then
            yBird = yLand - eps
            crash()
        end
        bird.x = xBird
        bird.y = yBird
        if gameStatus == 1 then
            bird.rotation = -30 * math.atan(vBird / uBird)
        else
            bird.rotation = vBird / 8
        end
    end
end

local function setupLand()
    land = display.newImageRect("Assets/land.png", display.actualContentWidth * 2, hLand * 2)
    land.x = xLand
    land.y = yLand + hLand
end

local function setupImages()
    local ground = display.newImageRect("Assets/ground.png", display.actualContentWidth, display.actualContentHeight)
    ground.x = display.contentCenterX
    ground.y = display.contentCenterY
    ground:addEventListener("tap", wing)

    for i = 1, 3 do
        pipes[i] = display.newImageRect("Assets/pipe.png", 80, 1000)
        pipes[i].x = 440 + wPipe * (i - 1)
        pipes[i].y = calcRandomHole()
    end

    getReady = display.newImageRect("Assets/getready.png", 200, 60)
    getReady.x = display.contentCenterX
    getReady.y = yReady
    getReady.alpha = 0

    gameOver = display.newImageRect("Assets/gameover.png", 200, 60)
    gameOver.x = display.contentCenterX
    gameOver.y = 0
    gameOver.alpha = 0

    board = display.newGroup()
    local img = display.newImageRect(board, "Assets/board.png", 240, 140)

    scoreTitle = display.newText(board, score, 80, -18, "Assets/troika.otf", 21)
    scoreTitle:setFillColor(0.75, 0, 0)
    bestTitle = display.newText(board, bestScore, 80, 24, "Assets/troika.otf", 21)
    bestTitle:setFillColor(0.75, 0, 0)

    silver = display.newImageRect(board, "Assets/silver.png", 44, 44)
    silver.x = -64
    silver.y = 4

    gold = display.newImageRect(board, "Assets/gold.png", 44, 44)
    gold.x = -64
    gold.y = 4

    board.x = display.contentCenterX
    board.y = 0
    board.alpha = 0

    local txt = {
        x = display.contentCenterX,
        y = 60,
        text = "",
        font = "Assets/troika.otf",
        fontSize = 35
    }

    title = display.newText(txt)
    title:setFillColor(1, 1, 1)
end

-- Start application point
loadSounds()
setupImages()
setupBird()
setupExplosion()
setupLand()
initGame()
loadBestScore()
gameLoopTimer = timer.performWithDelay(25, gameLoop, 0)

display.setStatusBar(display.HiddenStatusBar)
