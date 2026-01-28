const fs = require('fs');
const path = require('path');

const MIN_COVERAGE = 60;

function findCoverageIndexPath() {
  const coverageRoot = path.join(__dirname, '..', 'coverage');
  const candidates = [
    path.join(coverageRoot, 'project-management-frontend', 'index.html'),
    path.join(coverageRoot, 'index.html')
  ];

  for (const candidate of candidates) {
    if (fs.existsSync(candidate)) {
      return candidate;
    }
  }

  if (fs.existsSync(coverageRoot)) {
    const stack = [coverageRoot];
    while (stack.length > 0) {
      const current = stack.pop();
      const entries = fs.readdirSync(current, { withFileTypes: true });
      for (const entry of entries) {
        const fullPath = path.join(current, entry.name);
        if (entry.isDirectory()) {
          stack.push(fullPath);
        } else if (entry.isFile() && entry.name === 'index.html') {
          const content = fs.readFileSync(fullPath, 'utf8');
          if (content.includes('<h1>All files</h1>')) {
            return fullPath;
          }
        }
      }
    }
  }

  return null;
}

function parseCoverageFromIndexHtml(indexHtmlPath) {
  const content = fs.readFileSync(indexHtmlPath, 'utf8');
  const metrics = ['Statements', 'Branches', 'Functions', 'Lines'];
  const result = {};

  for (const metric of metrics) {
    const regex = new RegExp(
      `<span class="strong">\\s*([0-9]+(?:\\.[0-9]+)?)%\\s*</span>\\s*` +
      `<span class="quiet">${metric}</span>`,
      'i'
    );
    const match = content.match(regex);
    if (match) {
      result[metric.toLowerCase()] = parseFloat(match[1]);
    }
  }

  return result;
}

function readCoverageSummary() {
  const indexPath = findCoverageIndexPath();
  if (!indexPath) {
    throw new Error('Coverage report not found under frontend/coverage');
  }

  return { total: parseCoverageFromIndexHtml(indexPath) };
}

function ensureThresholds(summary) {
  const total = summary.total;
  const checks = [
    { name: 'statements', pct: total.statements },
    { name: 'branches', pct: total.branches },
    { name: 'functions', pct: total.functions },
    { name: 'lines', pct: total.lines }
  ];

  const failures = checks.filter(check => check.pct < MIN_COVERAGE);

  if (failures.length > 0) {
    const details = failures
      .map(check => `${check.name}=${check.pct}%`)
      .join(', ');
    throw new Error(
      `Coverage below ${MIN_COVERAGE}%: ${details}`
    );
  }
}

try {
  const summary = readCoverageSummary();
  ensureThresholds(summary);
  console.log(`Coverage OK (>= ${MIN_COVERAGE}%)`);
} catch (error) {
  console.error(error.message);
  process.exit(1);
}
