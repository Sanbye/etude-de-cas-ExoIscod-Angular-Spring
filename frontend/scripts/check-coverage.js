const fs = require('fs');
const path = require('path');

const SUMMARY_PATH = path.join(
  __dirname,
  '..',
  'coverage',
  'project-management-frontend',
  'coverage-summary.json'
);

const MIN_COVERAGE = 60;

function readCoverageSummary() {
  if (!fs.existsSync(SUMMARY_PATH)) {
    throw new Error(`Coverage summary not found at ${SUMMARY_PATH}`);
  }

  const raw = fs.readFileSync(SUMMARY_PATH, 'utf8');
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
