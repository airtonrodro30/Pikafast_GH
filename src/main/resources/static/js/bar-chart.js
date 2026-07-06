document.addEventListener('DOMContentLoaded', function () {
    var chartDom = document.getElementById('bar-chart');
    var chartTypeSelect = document.getElementById('chart-type');
    var barChartPanel = document.getElementById('bar-chart-panel');
    var pieChartPanel = document.getElementById('pie-chart-panel');
    var barColors = [
        '#4C9F38',
        '#F39C12',
        '#3498DB',
        '#E74C3C',
        '#9B59B6',
        '#1ABC9C',
        '#E67E22',
        '#2ECC71',
        '#34495E',
        '#D35400'
    ];

    if (!chartDom || !chartTypeSelect || typeof echarts === 'undefined') {
        return;
    }

    var myChart = echarts.init(chartDom);

    function actualizarVisibilidad(tipo) {
        var mostrarPie = tipo === 'pedidos-por-mes';

        if (barChartPanel) {
            barChartPanel.style.display = mostrarPie ? 'none' : '';
        }

        if (pieChartPanel) {
            pieChartPanel.style.display = mostrarPie ? '' : 'none';
        }
    }

    async function cargarGrafico(tipo) {
        if (tipo === 'pedidos-por-mes') {
            return;
        }

        try {
var response = await fetch('/api/v1/dashboard/chart?tipo=' + encodeURIComponent(tipo));

            if (!response.ok) {
                throw new Error('No se pudo cargar la data del grafico');
            }

            var data = await response.json();
            var seriesData = data.values.map(function (value, index) {
                return {
                    value: value,
                    itemStyle: {
                        color: barColors[index % barColors.length]
                    }
                };
            });

            var option = {
                title: {
                    text: data.title
                },
                tooltip: {},
                xAxis: {
                    type: 'category',
                    data: data.labels,
                    axisLabel: {
                        interval: 0,
                        rotate: 30
                    }
                },
                yAxis: {
                    type: 'value'
                },
                series: [
                    {
                        name: 'Total',
                        type: 'bar',
                        data: seriesData
                    }
                ]
            };

            myChart.setOption(option, true);
        } catch (error) {
            console.error('Error al renderizar el grafico:', error);
        }
    }

    chartTypeSelect.addEventListener('change', function () {
        actualizarVisibilidad(this.value);
        cargarGrafico(this.value);
    });

    window.addEventListener('resize', function () {
        if (barChartPanel && barChartPanel.style.display !== 'none') {
            myChart.resize();
        }
    });

    actualizarVisibilidad(chartTypeSelect.value);
    cargarGrafico(chartTypeSelect.value);
});
