// NPF Indicator page: renders Highcharts charts from embedded data attributes

'use strict';

import Highcharts from 'highcharts';
import 'highcharts/modules/accessibility';

/**
 * Parse a CSV string into { headers: string[], rows: string[][] }
 */
function parseCsv(csv) {
    const lines = csv.trim().split(/\r?\n/);
    if (lines.length < 2) {
        return { headers: [], rows: [] };
    }
    const headers = splitCsvLine(lines[0]);
    const rows = lines.slice(1).map(splitCsvLine);
    return { headers, rows };
}

function splitCsvLine(line) {
    const result = [];
    let current = '';
    let inQuotes = false;

    for (let i = 0; i < line.length; i++) {
        const ch = line[i];
        if (ch === '"') {
            inQuotes = !inQuotes;
        } else if (ch === ',' && !inQuotes) {
            result.push(current.trim());
            current = '';
        } else {
            current += ch;
        }
    }
    result.push(current.trim());
    return result;
}

/**
 * Convert parsed CSV to Highcharts series.
 * First column = x-axis categories; remaining columns = numeric series.
 */
function csvToSeries(parsed) {
    const { headers, rows } = parsed;
    if (!headers.length || !rows.length) {
        return { categories: [], series: [] };
    }

    const categories = rows.map(row => row[0]);
    const series = headers.slice(1).map((name, colIndex) => ({
        name,
        data: rows.map(row => {
            const val = parseFloat(row[colIndex + 1]);
            return isNaN(val) ? null : val;
        })
    }));

    return { categories, series };
}

/**
 * Render a single chart container.
 * The element must have:
 *   data-csv    — raw CSV text (first column = categories, rest = series)
 *   data-config — JSON Highcharts options (series and xAxis.categories injected from CSV)
 */
function renderChart(el) {
    const csv = el.getAttribute('data-csv') || '';
    const configJson = el.getAttribute('data-config') || '{}';

    let config = {};
    try {
        config = JSON.parse(configJson);
    } catch (e) {
        console.error('NPF chart: invalid JSON config', e);
    }

    const parsed = parseCsv(csv);
    const { categories, series } = csvToSeries(parsed);

    config.xAxis = config.xAxis || {};
    if (!config.xAxis.categories) {
        config.xAxis.categories = categories;
    }

    if (!config.series || !config.series.length) {
        config.series = series;
    }

    config.credits = config.credits || { enabled: false };

    Highcharts.chart(el, config);
}

function init() {
    const chartContainers = [].slice.call(document.querySelectorAll('.gov_npf-chart'));
    chartContainers.forEach(renderChart);
}

init();

export default { init };
