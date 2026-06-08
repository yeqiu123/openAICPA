const STORAGE_KEY = "openai-account-panel:v1";

const state = {
  view: "accounts",
  accounts: [],
  proxies: [],
  selectedIds: new Set(),
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
    JSON.stringify({ version: 1, updatedAt: nowISO(), accounts: state.accounts, proxies: state.proxies })
  );
}

function parseJsonObject(text, fallback = {}) {
  const trimmed = String(text || "").trim();
  if (!trimmed) return fallback;
  const parsed = JSON.parse(trimmed);
  return parsed && typeof parsed === "object" && !Array.isArray(parsed) ? parsed : fallback;
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
  const extra = input.extra && typeof input.extra === "object" ? input.extra : {};
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
    headers: input.headers && typeof input.headers === "object" ? input.headers : extra.headers || {},
    modelMapping:
      input.modelMapping && typeof input.modelMapping === "object"
        ? input.modelMapping
        : extra.model_mapping || extra.modelMapping || {},
    extra,
    proxyKey: input.proxyKey || extra.proxy_key || "",
    expiresAt: input.expiresAt || input.expires_at || extra.expires_at || "",
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

function quotaBarClass(percent) {
  if (percent >= 95) return "quota-fill danger";
  if (percent >= 80) return "quota-fill warn";
  return "quota-fill ok";
}

function quotaHealth(account) {
  if (!account.quotaLimit) return "不限额度";
  const percent = quotaPercent(account);
  if (percent >= 95) return "额度耗尽";
  if (percent >= 80) return "接近上限";
  return "额度正常";
}

function deriveQuotaStatus(account) {
  if (!account.quotaLimit) return account.status === "disabled" ? "disabled" : "active";
  const percent = quotaPercent(account);
  if (percent >= 100) return "disabled";
  if (percent >= 80) return "warning";
  return "active";
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
  const risky = state.accounts.filter((item) => item.quotaLimit && quotaPercent(item) >= 80).length;
  return { total, active, quotaUsed, quotaLimit, tokens, risky };
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
    ${state.view === "quota" ? renderQuotaTable() : state.view === "usage" ? renderUsageView() : renderAccountTable()}
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
  const selected = selectedAccounts();
  const allVisibleSelected = rows.length > 0 && rows.every((item) => state.selectedIds.has(item.id));
  return `
    <section class="card section">
      <div class="section-head">
        <div class="section-title">${state.view === "usage" ? "用量概览" : "账号列表"}</div>
        <div class="muted">${rows.length} 个结果</div>
      </div>
      <div class="batchbar ${selected.length ? "show" : ""}">
        <div>已选择 ${selected.length} 个账号</div>
        <div class="actions">
          <button class="small" id="batchEnableBtn">启用</button>
          <button class="small" id="batchDisableBtn">停用</button>
          <button class="small" id="batchExportBtn">导出所选</button>
          <button class="small danger" id="batchDeleteBtn">删除</button>
        </div>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th class="check-cell"><input id="selectAllVisible" type="checkbox" ${allVisibleSelected ? "checked" : ""} /></th>
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
            ${rows.map(renderAccountRow).join("") || `<tr><td colspan="9"><div class="empty">暂无账号</div></td></tr>`}
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
      <td class="check-cell"><input type="checkbox" data-select="${account.id}" ${state.selectedIds.has(account.id) ? "checked" : ""} /></td>
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
        <div class="row-actions">
          <button class="small" data-copy="${account.id}">复制</button>
          <button class="small" data-clone="${account.id}">克隆</button>
          <button class="small" data-edit="${account.id}">编辑</button>
          <button class="small danger" data-delete="${account.id}">删除</button>
        </div>
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
              <th>剩余额度</th>
              <th>请求数</th>
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
  const remain = account.quotaLimit ? Math.max(account.quotaLimit - account.quotaUsed, 0) : 0;
  return `
    <tr>
      <td><strong>${escapeHtml(account.name)}</strong><div class="muted">${escapeHtml(account.provider)}</div></td>
      <td>
        <div class="quota-track"><span class="${quotaBarClass(percent)}" style="width:${account.quotaLimit ? percent : 100}%"></span></div>
        <div class="muted">${account.quotaLimit ? `${percent}%` : "不限额度"}</div>
      </td>
      <td>${formatNumber(account.quotaUsed)}</td>
      <td>${account.quotaLimit ? formatNumber(account.quotaLimit) : "不限"}</td>
      <td>${account.quotaLimit ? formatNumber(remain) : "不限"}</td>
      <td>${formatNumber(account.requestCount)}</td>
      <td><span class="pill ${statusClass(account.status)}">${statusText(account.status)}</span><div class="muted">${quotaHealth(account)}</div></td>
      <td>
        <div class="row-actions">
          <button class="small" data-usage="${account.id}">录入</button>
          <button class="small" data-edit="${account.id}">编辑</button>
          <button class="small" data-auto-status="${account.id}">同步状态</button>
          <button class="small danger" data-reset-usage="${account.id}">重置</button>
        </div>
      </td>
    </tr>
  `;
}

function renderUsageView() {
  const rows = filteredAccounts();
  const providers = providerUsage(rows);
  return `
    <div class="usage-grid">
      ${providers.map(renderProviderUsage).join("") || `<div class="card stat"><div class="stat-label">暂无用量</div><div class="stat-value">0</div></div>`}
    </div>
    <section class="card section" style="margin-top:14px">
      <div class="section-head">
        <div class="section-title">用量流水</div>
        <div class="muted">录入请求数、Token 与额度消耗</div>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>账号</th>
              <th>提供商</th>
              <th>请求数</th>
              <th>Token</th>
              <th>额度消耗</th>
              <th>最近记录</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            ${rows.map(renderUsageRow).join("") || `<tr><td colspan="7"><div class="empty">暂无用量数据</div></td></tr>`}
          </tbody>
        </table>
      </div>
    </section>
  `;
}

function providerUsage(rows) {
  const map = new Map();
  rows.forEach((account) => {
    const item = map.get(account.provider) || { provider: account.provider, accounts: 0, requests: 0, tokens: 0, quota: 0 };
    item.accounts += 1;
    item.requests += Number(account.requestCount || 0);
    item.tokens += Number(account.tokenUsed || 0);
    item.quota += Number(account.quotaUsed || 0);
    map.set(account.provider, item);
  });
  return [...map.values()].sort((a, b) => b.quota - a.quota).slice(0, 6);
}

function renderProviderUsage(item) {
  return `
    <div class="card stat">
      <div class="stat-label">${escapeHtml(item.provider)} · ${item.accounts} 账号</div>
      <div class="stat-value">${formatNumber(item.quota)}</div>
      <div class="muted">${formatNumber(item.requests)} 请求 · ${formatNumber(item.tokens)} Token</div>
    </div>
  `;
}

function renderUsageRow(account) {
  const logs = Array.isArray(account.extra?.usage_logs) ? account.extra.usage_logs : [];
  const latest = logs[0];
  return `
    <tr>
      <td><strong>${escapeHtml(account.name)}</strong><div class="muted">${escapeHtml(account.baseUrl)}</div></td>
      <td><span class="pill">${escapeHtml(account.provider)}</span></td>
      <td>${formatNumber(account.requestCount)}</td>
      <td>${formatNumber(account.tokenUsed)}</td>
      <td>${formatNumber(account.quotaUsed)}</td>
      <td>${latest ? `${escapeHtml(latest.time)}<div class="muted">${escapeHtml(latest.note || "")}</div>` : "-"}</td>
      <td>
        <div class="row-actions">
          <button class="small" data-usage="${account.id}">录入用量</button>
          <button class="small danger" data-reset-usage="${account.id}">重置用量</button>
        </div>
      </td>
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
      <div class="format-grid">
        <div><strong>new-api</strong><span>识别 channels/data 数组，字段包含 key、base_url、models、group、used_quota。</span></div>
        <div><strong>sub2api</strong><span>识别 data.accounts/accounts 与 proxies，保留 credentials、extra、proxy_key。</span></div>
        <div><strong>CPA</strong><span>识别 openai-compatibility、api-keys、gemini/codex/claude/vertex 配置。</span></div>
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
  document.querySelectorAll("[data-select]").forEach((input) => {
    input.addEventListener("change", () => {
      if (input.checked) state.selectedIds.add(input.dataset.select);
      else state.selectedIds.delete(input.dataset.select);
      renderView();
    });
  });
  const selectAll = document.querySelector("#selectAllVisible");
  if (selectAll) {
    selectAll.addEventListener("change", () => {
      filteredAccounts().forEach((account) => {
        if (selectAll.checked) state.selectedIds.add(account.id);
        else state.selectedIds.delete(account.id);
      });
      renderView();
    });
  }
  document.querySelectorAll("[data-copy]").forEach((button) => {
    button.addEventListener("click", () => copyAccountConfig(button.dataset.copy));
  });
  document.querySelectorAll("[data-clone]").forEach((button) => {
    button.addEventListener("click", () => cloneAccount(button.dataset.clone));
  });
  document.querySelectorAll("[data-delete]").forEach((button) => {
    button.addEventListener("click", () => deleteAccount(button.dataset.delete));
  });
  document.querySelectorAll("[data-usage]").forEach((button) => {
    button.addEventListener("click", () => openUsageModal(button.dataset.usage));
  });
  document.querySelectorAll("[data-reset-usage]").forEach((button) => {
    button.addEventListener("click", () => resetUsage(button.dataset.resetUsage));
  });
  document.querySelectorAll("[data-auto-status]").forEach((button) => {
    button.addEventListener("click", () => syncQuotaStatus(button.dataset.autoStatus));
  });
  const batchEnable = document.querySelector("#batchEnableBtn");
  const batchDisable = document.querySelector("#batchDisableBtn");
  const batchExport = document.querySelector("#batchExportBtn");
  const batchDelete = document.querySelector("#batchDeleteBtn");
  if (batchEnable) batchEnable.addEventListener("click", () => batchSetStatus("active"));
  if (batchDisable) batchDisable.addEventListener("click", () => batchSetStatus("disabled"));
  if (batchExport) batchExport.addEventListener("click", () => downloadJson("selected-accounts.json", buildUnifiedExport(selectedAccounts())));
  if (batchDelete) batchDelete.addEventListener("click", batchDeleteAccounts);
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
            ${formTextarea("headers", "请求头 JSON", JSON.stringify(item.headers || {}, null, 2))}
            ${formTextarea("modelMapping", "模型映射 JSON", JSON.stringify(item.modelMapping || {}, null, 2))}
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

function openUsageModal(id) {
  const account = state.accounts.find((item) => item.id === id);
  if (!account) return;
  const modal = document.querySelector("#modalRoot");
  modal.className = "modal-backdrop open";
  modal.innerHTML = `
    <div class="modal">
      <div class="modal-head">
        <div>
          <div class="section-title">录入用量</div>
          <div class="muted">${escapeHtml(account.name)}</div>
        </div>
        <button id="closeModalBtn">关闭</button>
      </div>
      <form id="usageForm">
        <div class="modal-body">
          <div class="form-grid">
            ${formInput("requestDelta", "新增请求数", 1, "number")}
            ${formInput("tokenDelta", "新增 Token", 0, "number")}
            ${formInput("quotaDelta", "新增额度消耗", 0, "number")}
            ${formInput("quotaLimit", "额度上限", account.quotaLimit, "number")}
            ${formTextarea("note", "备注", "")}
          </div>
        </div>
        <div class="modal-foot">
          <button type="button" id="cancelModalBtn">取消</button>
          <button class="primary" type="submit">保存用量</button>
        </div>
      </form>
    </div>
  `;
  document.querySelector("#closeModalBtn").addEventListener("click", closeModal);
  document.querySelector("#cancelModalBtn").addEventListener("click", closeModal);
  document.querySelector("#usageForm").addEventListener("submit", (event) => {
    event.preventDefault();
    saveUsage(id, new FormData(event.currentTarget));
  });
}

function saveAccount(form, id) {
  const existing = state.accounts.find((item) => item.id === id);
  let headers = {};
  let modelMapping = {};
  try {
    headers = parseJsonObject(form.get("headers"));
    modelMapping = parseJsonObject(form.get("modelMapping"));
  } catch (error) {
    showToast(`JSON 字段格式错误：${error.message}`);
    return;
  }
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
    headers,
    modelMapping,
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

function formNumber(form, name) {
  const value = Number(form.get(name) || 0);
  return Number.isFinite(value) ? value : 0;
}

function saveUsage(id, form) {
  const note = String(form.get("note") || "").trim();
  state.accounts = state.accounts.map((account) => {
    if (account.id !== id) return account;
    const extra = { ...(account.extra || {}) };
    const logs = Array.isArray(extra.usage_logs) ? extra.usage_logs : [];
    const next = normalizeAccount({
      ...account,
      requestCount: Number(account.requestCount || 0) + formNumber(form, "requestDelta"),
      tokenUsed: Number(account.tokenUsed || 0) + formNumber(form, "tokenDelta"),
      quotaUsed: Number(account.quotaUsed || 0) + formNumber(form, "quotaDelta"),
      quotaLimit: formNumber(form, "quotaLimit"),
      extra: {
        ...extra,
        // 保留最近的手工录入记录，便于回看额度变化来源。
        usage_logs: [
          {
            time: nowISO(),
            requests: formNumber(form, "requestDelta"),
            tokens: formNumber(form, "tokenDelta"),
            quota: formNumber(form, "quotaDelta"),
            note,
          },
          ...logs,
        ].slice(0, 20),
      },
    });
    return normalizeAccount({ ...next, status: deriveQuotaStatus(next) });
  });
  writeStore();
  closeModal();
  renderView();
  showToast("用量已更新");
}

function resetUsage(id) {
  state.accounts = state.accounts.map((account) => {
    if (account.id !== id) return account;
    return normalizeAccount({
      ...account,
      quotaUsed: 0,
      requestCount: 0,
      tokenUsed: 0,
      status: "active",
      extra: { ...(account.extra || {}), usage_logs: [] },
    });
  });
  writeStore();
  renderView();
  showToast("用量已重置");
}

function syncQuotaStatus(id) {
  state.accounts = state.accounts.map((account) => {
    if (account.id !== id) return account;
    return normalizeAccount({ ...account, status: deriveQuotaStatus(account) });
  });
  writeStore();
  renderView();
  showToast("状态已按额度同步");
}

function deleteAccount(id) {
  state.accounts = state.accounts.filter((item) => item.id !== id);
  state.selectedIds.delete(id);
  writeStore();
  renderView();
  showToast("账号已删除");
}

function selectedAccounts() {
  return state.accounts.filter((item) => state.selectedIds.has(item.id));
}

function cloneAccount(id) {
  const account = state.accounts.find((item) => item.id === id);
  if (!account) return;
  const cloned = normalizeAccount({
    ...account,
    id: uid(),
    name: `${account.name} 副本`,
    source: "clone",
    createdAt: nowISO(),
  });
  state.accounts.unshift(cloned);
  writeStore();
  renderView();
  showToast("账号已克隆");
}

function batchSetStatus(status) {
  const ids = new Set([...state.selectedIds]);
  state.accounts = state.accounts.map((account) =>
    ids.has(account.id) ? normalizeAccount({ ...account, status }) : account
  );
  state.selectedIds.clear();
  writeStore();
  renderView();
  showToast("批量状态已更新");
}

function batchDeleteAccounts() {
  const ids = new Set([...state.selectedIds]);
  state.accounts = state.accounts.filter((account) => !ids.has(account.id));
  state.selectedIds.clear();
  writeStore();
  renderView();
  showToast("已删除所选账号");
}

async function copyAccountConfig(id) {
  const account = state.accounts.find((item) => item.id === id);
  if (!account) return;
  const command = [
    "curl",
    `${account.baseUrl.replace(/\/$/, "")}/chat/completions`,
    "-H",
    `"Authorization: Bearer ${account.apiKey || "<api-key>"}"`,
    "-H",
    '"Content-Type: application/json"',
    "-d",
    `'{"model":"${account.models[0] || "gpt-4o-mini"}","messages":[{"role":"user","content":"ping"}]}'`,
  ].join(" ");

  try {
    await navigator.clipboard.writeText(command);
    showToast("调用示例已复制");
  } catch {
    openCopyModal(command);
  }
}

function openCopyModal(text) {
  const modal = document.querySelector("#modalRoot");
  modal.className = "modal-backdrop open";
  modal.innerHTML = `
    <div class="modal">
      <div class="modal-head">
        <div class="section-title">复制调用示例</div>
        <button id="closeModalBtn">关闭</button>
      </div>
      <div class="modal-body">
        <div class="copy-box">${escapeHtml(text)}</div>
      </div>
    </div>
  `;
  document.querySelector("#closeModalBtn").addEventListener("click", closeModal);
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

function buildUnifiedExport(accounts = state.accounts) {
  return {
    type: "openai-account-panel",
    version: 1,
    exported_at: nowISO(),
    proxies: state.proxies,
    accounts,
  };
}

function buildNewApiExport(accounts = state.accounts) {
  return {
    channels: accounts.map((account) => ({
      id: Number(account.extra?.newApiId || 0),
      type: account.extra?.newApiType ? Number(account.extra.newApiType) : providerToNewApiType(account.provider),
      key: account.apiKey,
      status: account.status === "active" ? 1 : 2,
      name: account.name,
      openai_organization: account.extra?.openai_organization || undefined,
      test_model: account.extra?.test_model || account.models[0] || undefined,
      base_url: account.baseUrl,
      balance: account.quotaLimit ? Math.max(account.quotaLimit - account.quotaUsed, 0) : 0,
      models: account.models.join(","),
      group: account.tags.length ? account.tags.join(",") : "default",
      used_quota: account.quotaUsed,
      priority: account.priority,
      weight: account.extra?.weight || 0,
      model_mapping: Object.keys(account.modelMapping || {}).length ? JSON.stringify(account.modelMapping) : undefined,
      header_override: Object.keys(account.headers || {}).length ? JSON.stringify(account.headers) : undefined,
      other_info: JSON.stringify(account.extra || {}),
      remark: account.notes,
    })),
  };
}

function buildSub2ApiExport(accounts = state.accounts) {
  return {
    type: "sub2api-data",
    version: 1,
    exported_at: nowISO(),
    proxies: state.proxies,
    accounts: accounts.map((account) => ({
      name: account.name,
      notes: account.notes || undefined,
      platform: account.provider,
      type: account.accountType,
      credentials: {
        ...(account.credentials || {}),
        api_key: account.apiKey || account.credentials?.api_key,
        base_url: account.baseUrl,
        model_mapping: Object.keys(account.modelMapping || {}).length ? account.modelMapping : undefined,
      },
      extra: {
        ...(account.extra || {}),
        quota_limit: account.quotaLimit,
        quota_used: account.quotaUsed,
        request_count: account.requestCount,
        token_used: account.tokenUsed,
        models: account.models,
        tags: account.tags,
        headers: account.headers,
      },
      proxy_key: account.proxyKey || undefined,
      concurrency: account.concurrency,
      priority: account.priority,
      rate_multiplier: account.extra?.rate_multiplier,
      expires_at: account.expiresAt || undefined,
    })),
  };
}

function buildCpaExport(accounts = state.accounts) {
  const openaiCompatibility = accounts
    .filter((account) => !["gemini", "codex", "claude", "anthropic", "vertex"].includes(account.provider))
    .map((account) => ({
    name: account.name,
    prefix: account.extra?.prefix || account.provider,
    "base-url": account.baseUrl,
    "api-key-entries": [{ "api-key": account.apiKey, "auth-index": account.extra?.authIndex || account.id }],
    disabled: account.status !== "active",
    headers: account.headers && Object.keys(account.headers).length ? account.headers : undefined,
    models: account.models.map((name) => ({ name })),
    priority: account.priority,
  }));
  return {
    "api-keys": accounts.map((account) => account.apiKey).filter(Boolean),
    "gemini-api-key": accounts.filter((account) => account.provider === "gemini").map(toCpaProviderKey),
    "codex-api-key": accounts.filter((account) => account.provider === "codex").map(toCpaProviderKey),
    "claude-api-key": accounts.filter((account) => account.provider === "claude" || account.provider === "anthropic").map(toCpaProviderKey),
    "vertex-api-key": accounts.filter((account) => account.provider === "vertex").map(toCpaProviderKey),
    "openai-compatibility": openaiCompatibility,
  };
}

function toCpaProviderKey(account) {
  return {
    "api-key": account.apiKey,
    priority: account.priority,
    prefix: account.extra?.prefix || undefined,
    "base-url": account.baseUrl,
    "proxy-url": account.extra?.proxyUrl || account.extra?.["proxy-url"] || undefined,
    headers: account.headers && Object.keys(account.headers).length ? account.headers : undefined,
    models: account.models.map((name) => ({ name })),
    "excluded-models": account.extra?.excludedModels || account.extra?.["excluded-models"] || undefined,
    "auth-index": account.extra?.authIndex || account.id,
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
    const result = detectImportAccounts(parsed);
    if (!result.items.length) {
      showToast("没有识别到账号数据");
      return;
    }
    if (document.querySelector("#importMode").value === "replace") {
      state.accounts = [];
      state.proxies = [];
    }
    if (result.proxies?.length) {
      state.proxies = mergeProxies(result.proxies, state.proxies);
    }
    state.accounts = [...result.items, ...state.accounts];
    writeStore();
    renderLayout();
    showToast(`已导入 ${result.items.length} 个账号`);
  } catch (error) {
    showToast(`JSON 解析失败：${error.message}`);
  }
}

function detectImportAccounts(payload) {
  if (!payload || typeof payload !== "object") return importResult([]);
  if (Array.isArray(payload)) return importResult(payload.map(fromUnknownAccount).filter(Boolean));
  if (Array.isArray(payload.accounts) && payload.type === "openai-account-panel") {
    return importResult(payload.accounts.map(fromUnknownAccount).filter(Boolean), payload.proxies);
  }
  if (payload.data?.accounts) return importResult(payload.data.accounts.map(fromSub2ApiAccount).filter(Boolean), payload.data.proxies);
  if (Array.isArray(payload.channels)) return importResult(payload.channels.map(fromNewApiChannel).filter(Boolean));
  if (Array.isArray(payload.data)) return importResult(payload.data.map(fromNewApiChannel).filter(Boolean));
  if (payload["openai-compatibility"] || payload.openaiCompatibility || payload["api-keys"]) {
    return importResult(fromCpaConfig(payload));
  }
  if (Array.isArray(payload.accounts)) return importResult(payload.accounts.map(fromSub2ApiAccount).filter(Boolean), payload.proxies);
  return importResult([]);
}

function importResult(items, proxies = []) {
  return { items: items.filter(Boolean), proxies: Array.isArray(proxies) ? proxies : [] };
}

function mergeProxies(next, current) {
  const seen = new Set();
  return [...next, ...current].filter((proxy) => {
    const key = proxy.proxy_key || proxy.proxyKey || `${proxy.protocol}|${proxy.host}|${proxy.port}|${proxy.username || ""}|${proxy.password || ""}`;
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
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
    headers: parseMaybeJson(channel.header_override),
    modelMapping: parseMaybeJson(channel.model_mapping),
    notes: channel.remark,
    extra: {
      ...parseMaybeJson(channel.other_info),
      newApiId: channel.id,
      newApiType: channel.type,
      openai_organization: channel.openai_organization,
      test_model: channel.test_model,
      weight: channel.weight,
    },
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
    headers: extra.headers || credentials.headers || {},
    modelMapping: credentials.model_mapping || extra.model_mapping || {},
    notes: account.notes,
    credentials,
    extra,
    proxyKey: account.proxy_key,
    expiresAt: account.expires_at,
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
          headers: provider.headers || {},
          extra: { prefix: provider.prefix, authIndex: entry["auth-index"] || entry.authIndex, test_model: provider["test-model"] },
          source: "cpa",
        })
      );
    });
  });
  addCpaProviderKeyAccounts(accounts, "gemini", config["gemini-api-key"] || config.geminiApiKeys);
  addCpaProviderKeyAccounts(accounts, "codex", config["codex-api-key"] || config.codexApiKeys);
  addCpaProviderKeyAccounts(accounts, "claude", config["claude-api-key"] || config.claudeApiKeys);
  addCpaProviderKeyAccounts(accounts, "vertex", config["vertex-api-key"] || config.vertexApiKeys);
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

function addCpaProviderKeyAccounts(accounts, provider, list) {
  if (!Array.isArray(list)) return;
  list.forEach((item, index) => {
    const record = typeof item === "string" ? { "api-key": item } : item || {};
    const apiKey = record["api-key"] || record.apiKey;
    if (!apiKey) return;
    accounts.push(
      normalizeAccount({
        name: `${provider} ${index + 1}`,
        provider,
        accountType: "api_key",
        baseUrl: record["base-url"] || record.baseUrl || defaultBaseUrl(provider),
        apiKey,
        priority: record.priority,
        models: (record.models || []).map((model) => model.name || model),
        headers: record.headers || {},
        extra: {
          prefix: record.prefix,
          proxyUrl: record["proxy-url"] || record.proxyUrl,
          excludedModels: record["excluded-models"] || record.excludedModels,
          authIndex: record["auth-index"] || record.authIndex,
        },
        source: "cpa",
      })
    );
  });
}

function parseMaybeJson(value) {
  if (!value) return {};
  if (typeof value === "object" && !Array.isArray(value)) return value;
  try {
    const parsed = JSON.parse(String(value));
    return parsed && typeof parsed === "object" && !Array.isArray(parsed) ? parsed : {};
  } catch {
    return {};
  }
}

function inferProvider(type, baseUrl) {
  const url = String(baseUrl || "").toLowerCase();
  if (url.includes("anthropic")) return "anthropic";
  if (url.includes("openrouter")) return "openrouter";
  if (url.includes("vertex")) return "vertex";
  if (url.includes("codex")) return "codex";
  if (url.includes("gemini") || url.includes("google")) return "gemini";
  if (url.includes("openai")) return "openai";
  const map = { 14: "anthropic", 20: "openrouter", 24: "gemini", 41: "vertex", 57: "codex" };
  if (map[type]) return map[type];
  return "openai";
}

function providerToNewApiType(provider) {
  return { openai: 1, anthropic: 14, claude: 14, openrouter: 20, gemini: 24, vertex: 41, codex: 57 }[provider] || 1;
}

function defaultBaseUrl(provider) {
  return {
    openai: "https://api.openai.com/v1",
    anthropic: "https://api.anthropic.com",
    claude: "https://api.anthropic.com",
    gemini: "https://generativelanguage.googleapis.com",
    codex: "https://chatgpt.com",
    vertex: "https://aiplatform.googleapis.com",
  }[provider] || "https://api.openai.com/v1";
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
try {
  const raw = localStorage.getItem(STORAGE_KEY);
  const parsed = raw ? JSON.parse(raw) : {};
  state.proxies = Array.isArray(parsed.proxies) ? parsed.proxies : [];
} catch {
  state.proxies = [];
}
seedIfEmpty();
renderLayout();
