document.addEventListener('DOMContentLoaded', function () {
    var chartDom = document.getElementById('pie-chart');
    var chartTypeSelect = document.getElementById('chart-type');
    var pieChartPanel = document.getElementById('pie-chart-panel');
    var pieColors = [
        '#3498DB',
        '#1ABC9C',
        '#F39C12',
        '#E74C3C',
        '#9B59B6',
        '#2ECC71',
        '#E67E22',
        '#34495E',
        '#D35400',
        '#16A085',
        '#2980B9',
        '#C0392B'
    ];

    if (!chartDom || !chartTypeSelect || typeof echarts === 'undefined') {
        return;
    }

    var myChart;
    var dataCargada = false;

    function obtenerInstancia() {
        if (!myChart) {
            myChart = echarts.init(chartDom);
        }

        return myChart;
    }

    async function cargarGrafico() {
        try {
            var response = await fetch('/dashboard/api/chart?tipo=pedidos-por-mes');

            if (!response.ok) {
                throw new Error('No se pudo cargar la data del grafico circular');
            }

            var data = await response.json();
            var pieData = data.labels.map(function (label, index) {
                return {
                    name: label,
                    value: data.values[index],
                    itemStyle: {
                        color: pieColors[index % pieColors.length]
                    }
                };
            });

            var option = {
                title: {
                    text: data.title,
                    left: 'center'
                },
                tooltip: {
                    trigger: 'item'
                },
                legend: {
                    bottom: '0%',
                    left: 'center'
                },
                series: [
                    {
                        name: 'Pedidos',
                        type: 'pie',
                        radius: '60%',
                        data: pieData,
                        emphasis: {
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.3)'
                            }
                        },
                        label: {
                            formatter: '{b}: {c}'
                        }
                    }
                ]
            };

            obtenerInstancia().setOption(option, true);
            dataCargada = true;
        } catch (error) {
            console.error('Error al renderizar el grafico circular:', error);
        }
    }

    async function actualizarVisibilidad() {
        if (chartTypeSelect.value !== 'pedidos-por-mes') {
            return;
        }

        if (!dataCargada) {
            await cargarGrafico();
        } else if (pieChartPanel && pieChartPanel.style.display !== 'none') {
            obtenerInstancia().resize();
        }
    }

    chartTypeSelect.addEventListener('change', function () {
        actualizarVisibilidad();
    });

    window.addEventListener('resize', function () {
        if (myChart && pieChartPanel && pieChartPanel.style.display !== 'none') {
            myChart.resize();
        }
    });

    actualizarVisibilidad();
});
