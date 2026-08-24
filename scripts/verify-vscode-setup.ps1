$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$tasksPath = Join-Path $projectRoot '.vscode\tasks.json'
$launchPath = Join-Path $projectRoot '.vscode\launch.json'
$readmePath = Join-Path $projectRoot 'README.md'

function Assert-True {
    param([bool]$Condition, [string]$Message)
    if (-not $Condition) {
        throw $Message
    }
}

Assert-True (Test-Path $tasksPath) '缺少 .vscode/tasks.json'
Assert-True (Test-Path $launchPath) '缺少 .vscode/launch.json'

$tasksConfig = Get-Content -Raw $tasksPath | ConvertFrom-Json
$launchConfig = Get-Content -Raw $launchPath | ConvertFrom-Json
$readme = Get-Content -Raw $readmePath

$expectedTasks = @(
    '启动后端',
    '启动消费者 Web',
    '启动消费者移动端 H5',
    '启动微信小程序编译',
    '启动商家端',
    '启动管理端',
    '一键启动全部 Web 服务',
    '安装全部前端依赖',
    '运行后端测试',
    '构建全部前端'
)
$taskLabels = @($tasksConfig.tasks | ForEach-Object { $_.label })
foreach ($expectedTask in $expectedTasks) {
    Assert-True ($taskLabels -contains $expectedTask) "缺少 VS Code 任务：$expectedTask"
}

$expectedLaunches = @(
    '后端（Spring Boot）',
    '消费者 Web',
    '消费者移动端 H5',
    '微信小程序编译',
    '商家端',
    '管理端'
)
$launchNames = @($launchConfig.configurations | ForEach-Object { $_.name })
foreach ($expectedLaunch in $expectedLaunches) {
    Assert-True ($launchNames -contains $expectedLaunch) "缺少运行配置：$expectedLaunch"
}

$compoundNames = @($launchConfig.compounds | ForEach-Object { $_.name })
Assert-True ($compoundNames -contains '全栈：后端 + 消费者移动端') '缺少移动端全栈组合配置'
Assert-True ($compoundNames -contains '全栈：后端 + 全部 Web 前端') '缺少全部 Web 全栈组合配置'

foreach ($port in 5173, 5174, 5175, 5176) {
    Assert-True ($readme -match [regex]::Escape($port.ToString())) "README 缺少端口 $port"
}
Assert-True ($readme -match '终端.*运行任务') 'README 缺少“终端 → 运行任务”说明'
Assert-True ($readme -match '运行和调试') 'README 缺少“运行和调试”说明'
Assert-True ($readme -match 'jd_ecommerce') 'README 缺少开发数据库说明'

Write-Host 'VS Code 启动配置与 README 检查通过。'
