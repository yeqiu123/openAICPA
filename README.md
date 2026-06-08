# OpenAI Account Panel

一个轻量 Web 面板，用于管理 OpenAI 官方账号和第三方兼容 API 账号。当前版本为纯前端静态应用，数据保存在浏览器本地，便于直接发布到任意静态站点。

## 功能

- 账号管理：新增、编辑、删除、克隆、批量启停、批量导出。
- 用量管理：按账号录入请求数、Token、额度消耗，并按提供商汇总。
- 额度管理：显示额度进度、剩余额度、健康状态，支持重置用量和按额度同步状态。
- 导入导出：支持统一格式、new-api、sub2api、Cli-Proxy-API-Management-Center 配置 JSON。

## 使用

直接打开 `index.html` 即可使用。

构建静态文件：

```bash
npm run build
```

构建产物在 `dist/`，可直接上传到静态托管服务。

## JSON 兼容

- `new-api`：识别和导出 `channels` / `data` 数组，包含 `key`、`base_url`、`models`、`group`、`used_quota`、`model_mapping`、`header_override` 等字段。
- `sub2api`：识别和导出 `data.accounts` / `accounts` 与 `proxies`，保留 `credentials`、`extra`、`proxy_key`、`concurrency`、`priority`。
- `Cli-Proxy-API-Management-Center`：识别和导出 `api-keys`、`openai-compatibility`、`gemini-api-key`、`codex-api-key`、`claude-api-key`、`vertex-api-key`。

## 数据说明

当前数据只存储在浏览器 `localStorage` 中。导入导出会包含 API Key、OAuth Token 等敏感信息，请只在可信环境中使用和保存导出的 JSON。
