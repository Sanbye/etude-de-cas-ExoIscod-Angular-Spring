const fs = require('fs');
const path = require('path');

const MIN_COVERAGE = 60;

function findCoverageSummaryPath() {
  const coverageRoot = path.join(__dirname, '..', 'coverage');
  const candidates = [
    path.join(coverageRoot, 'project-management-frontend', 'coverage-summary.json'),
    path.join(coverageRoot, 'coverage-summary.json')
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
        } else if (entry.isFile() && entry.name === 'coverage-summary.json') {
          return fullPath;
        }
      }
    }
  }

  return null;
}

function readCoverageSummary() {
  const summaryPath = findCoverageSummaryPath();
  if (!summaryPath) {
    throw new Error('Coverage summary not found under frontend/coverage');
  }

  const raw = fs.readFileSync(summaryPath, 'utf8');
  return JSON.parse(raw);
}

function ensureThresholds(summary) {
  const total = summary.total;
  const checks = [
    { name: 'statements', pct: total.statements.pct },
    { name: 'branches', pct: total.branches.pct },
    { name: 'functions', pct: total.functions.pct },
    { name: 'lines', pct: total.lines.pct }
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
