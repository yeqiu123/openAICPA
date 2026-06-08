const STORAGE_KEY = "openai-account-panel:v1";

const state = {
  view: "accounts",
  accounts: [],
  filters: {
    keyword: "",
    provider: "all",
    status: "all",
    type: "all",
  },
};

const DEFAULT_MODELS = "gpt-4o,gpt-4o-mini,gpt-4.1,gpt-4.1-mini";

function uid() {
  return `acc_${Date.now().toString(36)}_${Math.random().toString(36).slice(2, 8)}`;
}

function nowISO() {
  return new Date().toISOString();
}

function readStore() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed.accounts) ? parsed.accounts : [];
  } catch {
    return [];
  }
}

function writeStore() {
  localStorage.setItem(
    STORAGE_KEY,
    JSON.stringify({ version: 1, updatedAt: nowISO(), accounts: state.accounts })
  );
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function maskSecret(value) {
  const text = String(value || "");
  if (!text) return "-";
  if (text.length <= 12) return `${text.slice(0, 3)}...`;
  return `${text.slice(0, 7)}...${text.slice(-4)}`;
}

function splitList(value) {
  return String(value || "")
    .split(/[\n,]/)
    .map((item) => item.trim())
    .filter(Boolean);
}

function normalizeAccount(input = {}) {
  return {
    id: input.id || uid(),
    name: String(input.name || "未命名账号"),
    provider: String(input.provider || "openai").toLowerCase(),
    accountType: String(input.accountType || "api_key"),
    baseUrl: String(input.baseUrl || "https://api.openai.com/v1"),
    apiKey: String(input.apiKey || ""),
    status: String(input.status || "active"),
    priority: Number(input.priority || 0),
    concurrency: Number(input.concurrency || 1),
    quotaLimit: Number(input.quotaLimit || 0),
    quotaUsed: Number(input.quotaUsed || 0),
    requestCount: Number(input.requestCount || 0),
    tokenUsed: Number(input.tokenUsed || 0),
    models: Array.isArray(input.models) ? input.models : splitList(input.models || DEFAULT_MODELS),
    tags: Array.isArray(input.tags) ? input.tags : splitList(input.tags || ""),
    notes: String(input.notes || ""),
    credentials: input.credentials && typeof input.credentials === "object" ? input.credentials : {},
    extra: input.extra && typeof input.extra === "object" ? input.extra : {},
    source: String(input.source || "manual"),
    createdAt: input.createdAt || nowISO(),
    updatedAt: nowISO(),
  };
}

function formatNumber(value) {
  return Number(value || 0).toLocaleString("zh-CN");
}

function quotaPercent(account) {
  if (!account.quotaLimit) return 0;
  return Math.min(100, Math.round((Number(account.quotaUsed || 0) / account.quotaLimit) * 100));
}

function statusClass(status) {
  if (status === "active") return "ok";
  if (status === "disabled") return "danger";
  return "warn";
}

function statusText(status) {
  return { active: "启用", disabled: "停用", warning: "关注" }[status] || status || "-";
}

function viewTitle() {
  if (state.view === "usage") return ["用量管理", "查看账号请求量、Token 消耗与额度使用情况。"];
  if (state.view === "quota") return ["额度管理", "快速维护额度上限、已用额度与状态。"];
  if (state.view === "transfer") return ["导入导出", "支持 new-api、sub2api、CPA 和统一格式 JSON。"];
  return ["账号管理", "维护 OpenAI 官方账号与第三方兼容 API。"];
}

function calcStats() {
  const total = state.accounts.length;
  const active = state.accounts.filter((item) => item.status === "active").length;
  const quotaUsed = state.accounts.reduce((sum, item) => sum + Number(item.quotaUsed || 0), 0);
  const quotaLimit = state.accounts.reduce((sum, item) => sum + Number(item.quotaLimit || 0), 0);
  const tokens = state.accounts.reduce((sum, item) => sum + Number(item.tokenUsed || 0), 0);
  return { total, active, quotaUsed, quotaLimit, tokens };
}

function filteredAccounts() {
  const keyword = state.filters.keyword.trim().toLowerCase();
  return state.accounts.filter((account) => {
    if (state.filters.provider !== "all" && account.provider !== state.filters.provider) return false;
    if (state.filters.status !== "all" && account.status !== state.filters.status) return false;
    if (state.filters.type !== "all" && account.accountType !== state.filters.type) return false;
    if (!keyword) return true;
    return [account.name, account.provider, account.baseUrl, account.apiKey, account.tags.join(",")]
      .join(" ")
      .toLowerCase()
      .includes(keyword);
  });
}

function renderLayout() {
  const [title, subtitle] = viewTitle();
  document.querySelector("#app").innerHTML = `
    <div class="shell">
      <aside class="sidebar">
        <div class="brand">
          <div class="brand-mark"></div>
          <div>
            <div class="brand-title">OpenAI 面板</div>
            <div class="brand-subtitle">账号 / 用量 / 额度</div>
          </div>
        </div>
        <nav class="nav">
          ${navButton("accounts", "账号管理")}
          ${navButton("usage", "用量管理")}
          ${navButton("quota", "额度管理")}
          ${navButton("transfer", "导入导出")}
        </nav>
      </aside>
      <main class="main">
        <div class="topbar">
          <div>
            <h1>${title}</h1>
            <p>${subtitle}</p>
          </div>
          <div class="actions">
            <button id="exportUnifiedBtn">导出统一 JSON</button>
            <button class="primary" id="addAccountBtn">新增账号</button>
          </div>
        </div>
        <div id="view"></div>
      </main>
    </div>
    <div id="modalRoot" class="modal-backdrop"></div>
    <div id="toast" class="toast"></div>
  `;
  bindCommonEvents();
  renderView();
}

function navButton(id, label) {
  return `<button class="${state.view === id ? "active" : ""}" data-view="${id}">${label}</button>`;
}

function renderView() {
  if (state.view === "transfer") {
    renderTransferView();
    return;
  }
  const stats = calcStats();
  document.querySelector("#view").innerHTML = `
    <div class="grid stats">
      <div class="card stat"><div class="stat-label">账号数</div><div class="stat-value">${stats.total}</div></div>
      <div class="card stat"><div class="stat-label">启用账号</div><div class="stat-value">${stats.active}</div></div>
      <div class="card stat"><div class="stat-label">已用额度</div><div class="stat-value">${formatNumber(stats.quotaUsed)}</div></div>
      <div class="card stat"><div class="stat-label">Token</div><div class="stat-value">${formatNumber(stats.tokens)}</div></div>
    </div>
    ${renderToolbar()}
    ${state.view === "quota" ? renderQuotaTable() : renderAccountTable()}
  `;
  bindViewEvents();
}

function renderToolbar() {
  const providers = [...new Set(state.accounts.map((item) => item.provider))].sort();
  return `
    <div class="toolbar">
      <input id="keywordFilter" placeholder="搜索名称、密钥、地址、标签" value="${escapeHtml(state.filters.keyword)}" />
      <select id="providerFilter">
        <option value="all">全部提供商</option>
        ${providers.map((item) => `<option value="${escapeHtml(item)}" ${state.filters.provider === item ? "selected" : ""}>${escapeHtml(item)}</option>`).join("")}
      </select>
      <select id="statusFilter">
        <option value="all">全部状态</option>
        <option value="active" ${state.filters.status === "active" ? "selected" : ""}>启用</option>
        <option value="warning" ${state.filters.status === "warning" ? "selected" : ""}>关注</option>
        <option value="disabled" ${state.filters.status === "disabled" ? "selected" : ""}>停用</option>
      </select>
      <select id="typeFilter">
        <option value="all">全部类型</option>
        <option value="api_key" ${state.filters.type === "api_key" ? "selected" : ""}>API Key</option>
        <option value="oauth" ${state.filters.type === "oauth" ? "selected" : ""}>OAuth</option>
        <option value="upstream" ${state.filters.type === "upstream" ? "selected" : ""}>Upstream</option>
      </select>
    </div>
  `;
}

function renderAccountTable() {
  const rows = filteredAccounts();
  return `
    <section class="card section">
      <div class="section-head">
        <div class="section-title">${state.view === "usage" ? "用量概览" : "账号列表"}</div>
        <div class="muted">${rows.length} 个结果</div>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>账号</th>
              <th>提供商</th>
              <th>地址</th>
              <th>密钥</th>
              <th>用量</th>
              <th>优先级</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            ${rows.map(renderAccountRow).join("") || `<tr><td colspan="8"><div class="empty">暂无账号</div></td></tr>`}
          </tbody>
        </table>
      </div>
    </section>
  `;
}

function renderAccountRow(account) {
  const percent = quotaPercent(account);
  return `
    <tr>
      <td>
        <strong>${escapeHtml(account.name)}</strong>
        <div class="muted">${escapeHtml(account.models.slice(0, 3).join(", "))}</div>
      </td>
      <td><span class="pill">${escapeHtml(account.provider)}</span><div class="muted">${escapeHtml(account.accountType)}</div></td>
      <td>${escapeHtml(account.baseUrl)}</td>
      <td>${escapeHtml(maskSecret(account.apiKey || account.credentials.access_token || account.credentials.token))}</td>
      <td>
        <div>${formatNumber(account.quotaUsed)} / ${account.quotaLimit ? formatNumber(account.quotaLimit) : "不限"}</div>
        <div class="muted">${percent}% · ${formatNumber(account.requestCount)} 请求 · ${formatNumber(account.tokenUsed)} Token</div>
      </td>
      <td>${formatNumber(account.priority)}<div class="muted">并发 ${formatNumber(account.concurrency)}</div></td>
      <td><span class="pill ${statusClass(account.status)}">${statusText(account.status)}</span></td>
      <td>
        <button data-edit="${account.id}">编辑</button>
        <button class="danger" data-delete="${account.id}">删除</button>
      </td>
    </tr>
  `;
}

function renderQuotaTable() {
  const rows = filteredAccounts();
  return `
    <section class="card section">
      <div class="section-head">
        <div class="section-title">额度列表</div>
        <div class="muted">可直接更新已用额度与上限</div>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>账号</th>
              <th>额度进度</th>
              <th>已用额度</th>
              <th>额度上限</th>
              <th>请求数</th>
              <th>Token</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            ${rows.map(renderQuotaRow).join("") || `<tr><td colspan="8"><div class="empty">暂无额度数据</div></td></tr>`}
          </tbody>
        </table>
      </div>
    </section>
  `;
}

function renderQuotaRow(account) {
  const percent = quotaPercent(account);
  return `
    <tr>
      <td><strong>${escapeHtml(account.name)}</strong><div class="muted">${escapeHtml(account.provider)}</div></td>
      <td>${percent}%<div class="muted">${account.quotaLimit ? "有限额度" : "不限额度"}</div></td>
      <td>${formatNumber(account.quotaUsed)}</td>
      <td>${account.quotaLimit ? formatNumber(account.quotaLimit) : "不限"}</td>
      <td>${formatNumber(account.requestCount)}</td>
      <td>${formatNumber(account.tokenUsed)}</td>
      <td><span class="pill ${statusClass(account.status)}">${statusText(account.status)}</span></td>
      <td><button data-edit="${account.id}">编辑额度</button></td>
    </tr>
  `;
}

function renderTransferView() {
  const unified = JSON.stringify(buildUnifiedExport(), null, 2);
  document.querySelector("#view").innerHTML = `
    <section class="card section">
      <div class="section-head">
        <div>
          <div class="section-title">导入</div>
          <div class="muted">粘贴 JSON 或选择文件，自动识别格式。</div>
        </div>
      </div>
      <div class="form-grid">
        <div class="form-field full">
          <label>JSON 内容</label>
          <textarea id="importText" placeholder='支持 {"accounts": []}、{"data":{"accounts":[]}}、{"channels": []} 等格式'></textarea>
        </div>
        <div class="form-field">
          <label>选择文件</label>
          <input id="importFile" type="file" accept=".json,application/json" />
        </div>
        <div class="form-field">
          <label>导入策略</label>
          <select id="importMode">
            <option value="merge">合并新增</option>
            <option value="replace">清空后导入</option>
          </select>
        </div>
      </div>
      <div class="actions" style="justify-content:flex-start;margin-top:12px">
        <button class="primary" id="importBtn">导入 JSON</button>
      </div>
    </section>
    <section class="card section" style="margin-top:14px">
      <div class="section-head">
        <div>
          <div class="section-title">导出</div>
          <div class="muted">按目标项目导出账号数据。</div>
        </div>
        <div class="actions">
          <button id="exportNewApiBtn">new-api</button>
          <button id="exportSub2ApiBtn">sub2api</button>
          <button id="exportCpaBtn">CPA</button>
          <button class="primary" id="exportUnifiedBtn2">统一格式</button>
        </div>
      </div>
      <textarea readonly style="min-height:320px">${escapeHtml(unified)}</textarea>
    </section>
  `;
  bindTransferEvents();
}

function bindCommonEvents() {
  document.querySelectorAll("[data-view]").forEach((button) => {
    button.addEventListener("click", () => {
      state.view = button.dataset.view;
      renderLayout();
    });
  });
  document.querySelector("#addAccountBtn").addEventListener("click", () => openAccountModal());
  document.querySelector("#exportUnifiedBtn").addEventListener("click", () => downloadJson("openai-panel-unified.json", buildUnifiedExport()));
}

function bindViewEvents() {
  const keyword = document.querySelector("#keywordFilter");
  if (keyword) {
    keyword.addEventListener("input", () => {
      state.filters.keyword = keyword.value;
      renderView();
    });
  }
  ["provider", "status", "type"].forEach((name) => {
    const input = document.querySelector(`#${name}Filter`);
    if (!input) return;
    input.addEventListener("change", () => {
      state.filters[name] = input.value;
      renderView();
    });
  });
  document.querySelectorAll("[data-edit]").forEach((button) => {
    button.addEventListener("click", () => {
      const account = state.accounts.find((item) => item.id === button.dataset.edit);
      openAccountModal(account);
    });
  });
  document.querySelectorAll("[data-delete]").forEach((button) => {
    button.addEventListener("click", () => deleteAccount(button.dataset.delete));
  });
}

function bindTransferEvents() {
  document.querySelector("#importFile").addEventListener("change", async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;
    document.querySelector("#importText").value = await file.text();
  });
  document.querySelector("#importBtn").addEventListener("click", importFromText);
  document.querySelector("#exportNewApiBtn").addEventListener("click", () => downloadJson("new-api-channels.json", buildNewApiExport()));
  document.querySelector("#exportSub2ApiBtn").addEventListener("click", () => downloadJson("sub2api-data.json", buildSub2ApiExport()));
  document.querySelector("#exportCpaBtn").addEventListener("click", () => downloadJson("cpa-config.json", buildCpaExport()));
  document.querySelector("#exportUnifiedBtn2").addEventListener("click", () => downloadJson("openai-panel-unified.json", buildUnifiedExport()));
}

function openAccountModal(account) {
  const editing = Boolean(account);
  const item = normalizeAccount(account || {});
  const modal = document.querySelector("#modalRoot");
  modal.className = "modal-backdrop open";
  modal.innerHTML = `
    <div class="modal">
      <div class="modal-head">
        <div class="section-title">${editing ? "编辑账号" : "新增账号"}</div>
        <button id="closeModalBtn">关闭</button>
      </div>
      <form id="accountForm">
        <div class="modal-body">
          <div class="form-grid">
            ${formInput("name", "名称", item.name)}
            ${formInput("provider", "提供商", item.provider)}
            ${formSelect("accountType", "类型", item.accountType, [["api_key", "API Key"], ["oauth", "OAuth"], ["upstream", "Upstream"]])}
            ${formSelect("status", "状态", item.status, [["active", "启用"], ["warning", "关注"], ["disabled", "停用"]])}
            ${formInput("baseUrl", "Base URL", item.baseUrl)}
            ${formInput("apiKey", "API Key / Token", item.apiKey)}
            ${formInput("priority", "优先级", item.priority, "number")}
            ${formInput("concurrency", "并发", item.concurrency, "number")}
            ${formInput("quotaUsed", "已用额度", item.quotaUsed, "number")}
            ${formInput("quotaLimit", "额度上限（0 表示不限）", item.quotaLimit, "number")}
            ${formInput("requestCount", "请求数", item.requestCount, "number")}
            ${formInput("tokenUsed", "Token 用量", item.tokenUsed, "number")}
            ${formTextarea("models", "模型（逗号或换行分隔）", item.models.join(","))}
            ${formTextarea("tags", "标签（逗号或换行分隔）", item.tags.join(","))}
            ${formTextarea("notes", "备注", item.notes)}
          </div>
        </div>
        <div class="modal-foot">
          <button type="button" id="cancelModalBtn">取消</button>
          <button class="primary" type="submit">保存</button>
        </div>
      </form>
    </div>
  `;
  document.querySelector("#closeModalBtn").addEventListener("click", closeModal);
  document.querySelector("#cancelModalBtn").addEventListener("click", closeModal);
  document.querySelector("#accountForm").addEventListener("submit", (event) => {
    event.preventDefault();
    saveAccount(new FormData(event.currentTarget), account?.id);
  });
}

function formInput(name, label, value, type = "text") {
  return `
    <div class="form-field">
      <label for="${name}">${label}</label>
      <input id="${name}" name="${name}" type="${type}" value="${escapeHtml(value)}" />
    </div>
  `;
}

function formSelect(name, label, value, options) {
  return `
    <div class="form-field">
      <label for="${name}">${label}</label>
      <select id="${name}" name="${name}">
        ${options.map(([id, text]) => `<option value="${id}" ${value === id ? "selected" : ""}>${text}</option>`).join("")}
      </select>
    </div>
  `;
}

function formTextarea(name, label, value) {
  return `
    <div class="form-field full">
      <label for="${name}">${label}</label>
      <textarea id="${name}" name="${name}">${escapeHtml(value)}</textarea>
    </div>
  `;
}

function closeModal() {
  const modal = document.querySelector("#modalRoot");
  modal.className = "modal-backdrop";
  modal.innerHTML = "";
}

function saveAccount(form, id) {
  const existing = state.accounts.find((item) => item.id === id);
  const next = normalizeAccount({
    ...existing,
    id,
    name: form.get("name"),
    provider: form.get("provider"),
    accountType: form.get("accountType"),
    baseUrl: form.get("baseUrl"),
    apiKey: form.get("apiKey"),
    status: form.get("status"),
    priority: form.get("priority"),
    concurrency: form.get("concurrency"),
    quotaUsed: form.get("quotaUsed"),
    quotaLimit: form.get("quotaLimit"),
    requestCount: form.get("requestCount"),
    tokenUsed: form.get("tokenUsed"),
    models: splitList(form.get("models")),
    tags: splitList(form.get("tags")),
    notes: form.get("notes"),
  });
  if (!next.name.trim()) {
    showToast("请填写账号名称");
    return;
  }
  if (id) {
    state.accounts = state.accounts.map((item) => (item.id === id ? next : item));
  } else {
    state.accounts.unshift(next);
  }
  writeStore();
  closeModal();
  renderView();
  showToast("账号已保存");
}

function deleteAccount(id) {
  state.accounts = state.accounts.filter((item) => item.id !== id);
  writeStore();
  renderView();
  showToast("账号已删除");
}

function showToast(message) {
  const toast = document.querySelector("#toast");
  toast.textContent = message;
  toast.className = "toast show";
  window.clearTimeout(showToast.timer);
  showToast.timer = window.setTimeout(() => {
    toast.className = "toast";
  }, 2200);
}

function downloadJson(filename, data) {
  const blob = new Blob([JSON.stringify(data, null, 2)], { type: "application/json" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}

function buildUnifiedExport() {
  return {
    type: "openai-account-panel",
    version: 1,
    exported_at: nowISO(),
    accounts: state.accounts,
  };
}

function buildNewApiExport() {
  return {
    channels: state.accounts.map((account) => ({
      id: Number(account.extra?.newApiId || 0),
      type: Number(account.extra?.newApiType || 1),
      key: account.apiKey,
      status: account.status === "active" ? 1 : 2,
      name: account.name,
      base_url: account.baseUrl,
      balance: account.quotaLimit ? Math.max(account.quotaLimit - account.quotaUsed, 0) : 0,
      models: account.models.join(","),
      group: account.tags.length ? account.tags.join(",") : "default",
      used_quota: account.quotaUsed,
      priority: account.priority,
      other_info: JSON.stringify(account.extra || {}),
      remark: account.notes,
    })),
  };
}

function buildSub2ApiExport() {
  return {
    type: "sub2api-data",
    version: 1,
    exported_at: nowISO(),
    proxies: [],
    accounts: state.accounts.map((account) => ({
      name: account.name,
      notes: account.notes || undefined,
      platform: account.provider,
      type: account.accountType,
      credentials: {
        ...(account.credentials || {}),
        api_key: account.apiKey || account.credentials?.api_key,
        base_url: account.baseUrl,
      },
      extra: {
        ...(account.extra || {}),
        quota_limit: account.quotaLimit,
        quota_used: account.quotaUsed,
        request_count: account.requestCount,
        token_used: account.tokenUsed,
        models: account.models,
        tags: account.tags,
      },
      concurrency: account.concurrency,
      priority: account.priority,
      rate_multiplier: account.extra?.rate_multiplier,
    })),
  };
}

function buildCpaExport() {
  const openaiCompatibility = state.accounts.map((account) => ({
    name: account.name,
    prefix: account.extra?.prefix || account.provider,
    "base-url": account.baseUrl,
    "api-key-entries": [{ "api-key": account.apiKey, "auth-index": account.id }],
    disabled: account.status !== "active",
    models: account.models.map((name) => ({ name })),
    priority: account.priority,
  }));
  return {
    "api-keys": state.accounts.map((account) => account.apiKey).filter(Boolean),
    "openai-compatibility": openaiCompatibility,
  };
}

function importFromText() {
  const text = document.querySelector("#importText").value.trim();
  if (!text) {
    showToast("请先填写 JSON 内容");
    return;
  }
  try {
    const parsed = JSON.parse(text);
    const accounts = detectImportAccounts(parsed);
    if (!accounts.length) {
      showToast("没有识别到账号数据");
      return;
    }
    if (document.querySelector("#importMode").value === "replace") {
      state.accounts = [];
    }
    state.accounts = [...accounts, ...state.accounts];
    writeStore();
    renderLayout();
    showToast(`已导入 ${accounts.length} 个账号`);
  } catch (error) {
    showToast(`JSON 解析失败：${error.message}`);
  }
}

function detectImportAccounts(payload) {
  if (Array.isArray(payload)) return payload.map(fromUnknownAccount).filter(Boolean);
  if (Array.isArray(payload.accounts) && payload.type === "openai-account-panel") {
    return payload.accounts.map(fromUnknownAccount).filter(Boolean);
  }
  if (Array.isArray(payload.channels)) return payload.channels.map(fromNewApiChannel).filter(Boolean);
  if (Array.isArray(payload.data)) return payload.data.map(fromNewApiChannel).filter(Boolean);
  if (payload.data?.accounts) return payload.data.accounts.map(fromSub2ApiAccount).filter(Boolean);
  if (Array.isArray(payload.accounts)) return payload.accounts.map(fromSub2ApiAccount).filter(Boolean);
  if (payload["openai-compatibility"] || payload.openaiCompatibility || payload["api-keys"]) {
    return fromCpaConfig(payload);
  }
  return [];
}

function fromUnknownAccount(item) {
  if (!item || typeof item !== "object") return null;
  return normalizeAccount(item);
}

function fromNewApiChannel(channel) {
  if (!channel || typeof channel !== "object") return null;
  return normalizeAccount({
    name: channel.name,
    provider: inferProvider(channel.type, channel.base_url),
    accountType: "api_key",
    baseUrl: channel.base_url,
    apiKey: channel.key,
    status: Number(channel.status) === 1 ? "active" : "disabled",
    priority: channel.priority,
    quotaUsed: channel.used_quota,
    quotaLimit: Number(channel.balance || 0) + Number(channel.used_quota || 0),
    models: splitList(channel.models),
    tags: splitList(channel.group),
    notes: channel.remark,
    extra: { newApiId: channel.id, newApiType: channel.type, other_info: channel.other_info },
    source: "new-api",
  });
}

function fromSub2ApiAccount(account) {
  if (!account || typeof account !== "object") return null;
  const credentials = account.credentials || {};
  const extra = account.extra || {};
  return normalizeAccount({
    name: account.name,
    provider: account.platform || "openai",
    accountType: account.type || "oauth",
    baseUrl: credentials.base_url || credentials.baseUrl || extra.base_url || "https://api.openai.com/v1",
    apiKey: credentials.api_key || credentials.apiKey || credentials.token || credentials.access_token || "",
    status: account.status || extra.status || "active",
    priority: account.priority,
    concurrency: account.concurrency,
    quotaLimit: extra.quota_limit,
    quotaUsed: extra.quota_used,
    requestCount: extra.request_count,
    tokenUsed: extra.token_used,
    models: extra.models || [],
    tags: extra.tags || [],
    notes: account.notes,
    credentials,
    extra,
    source: "sub2api",
  });
}

function fromCpaConfig(config) {
  const accounts = [];
  const providers = config["openai-compatibility"] || config.openaiCompatibility || [];
  providers.forEach((provider) => {
    const entries = provider["api-key-entries"] || provider.apiKeyEntries || [];
    entries.forEach((entry, index) => {
      accounts.push(
        normalizeAccount({
          name: entries.length > 1 ? `${provider.name || "openai"}-${index + 1}` : provider.name,
          provider: provider.name || provider.prefix || "openai",
          accountType: "api_key",
          baseUrl: provider["base-url"] || provider.baseUrl,
          apiKey: entry["api-key"] || entry.apiKey,
          status: provider.disabled ? "disabled" : "active",
          priority: provider.priority,
          models: (provider.models || []).map((item) => item.name || item),
          extra: { prefix: provider.prefix, authIndex: entry["auth-index"] || entry.authIndex },
          source: "cpa",
        })
      );
    });
  });
  const keys = config["api-keys"] || config.apiKeys || [];
  keys.forEach((key, index) => {
    if (accounts.some((account) => account.apiKey === key)) return;
    accounts.push(
      normalizeAccount({
        name: `CPA API Key ${index + 1}`,
        provider: "openai",
        accountType: "api_key",
        baseUrl: "https://api.openai.com/v1",
        apiKey: key,
        source: "cpa",
      })
    );
  });
  return accounts;
}

function inferProvider(type, baseUrl) {
  const url = String(baseUrl || "").toLowerCase();
  if (url.includes("anthropic")) return "anthropic";
  if (url.includes("gemini") || url.includes("google")) return "gemini";
  if (url.includes("openai")) return "openai";
  if (type === 14) return "anthropic";
  return "openai";
}

function seedIfEmpty() {
  if (state.accounts.length) return;
  state.accounts = [
    normalizeAccount({
      name: "OpenAI 官方账号",
      provider: "openai",
      accountType: "api_key",
      baseUrl: "https://api.openai.com/v1",
      apiKey: "sk-...",
      quotaLimit: 1000000,
      quotaUsed: 120000,
      requestCount: 320,
      tokenUsed: 680000,
      tags: ["default"],
      notes: "示例账号，可直接编辑或删除。",
    }),
  ];
  writeStore();
}

state.accounts = readStore();
seedIfEmpty();
renderLayout();
