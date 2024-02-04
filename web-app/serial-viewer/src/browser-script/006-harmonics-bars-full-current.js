class harmonicsBarsFullCurrent {
  constructor(inDOM, inHarmonicsData) {

    this.parent = inDOM;
    //create a new canvas inside parent
    this.canvas = document.createElement('canvas');
    this.parent.appendChild(this.canvas);
    //set canvas size
    this.canvas.width = this.parent.clientWidth;
    this.canvas.height = this.parent.clientHeight;

    this.harmonicsData = inHarmonicsData;
    this.genereateData();
    this.generateConfi();

    this.chart = new Chart(this.canvas,this.config);
    Object.seal(this.chart);
  }
  updateChart(newHarmonicsData) {
    this.harmonicsData = newHarmonicsData;
    let hiddenCharts = [];
    for (let i = 0; i < this.chart.data.datasets.length; i++) {
      if (this.chart.getDatasetMeta(i).hidden) {
        hiddenCharts.push(i);
      }
    }
    this.genereateData();
    for (let i = 0; i < hiddenCharts.length; i++) {
      if (hiddenCharts[i] < this.chart.data.datasets.length) {
        this.datasets[hiddenCharts[i]].hidden = true;
      }
    }
    this.chart.data = this.data;
    this.prepareChartXTitle();
    this.chart.update();

    //Update the meta data of hidden legends
    for (let i = 0; i < hiddenCharts.length; i++) {
      if (hiddenCharts[i] < this.chart.data.datasets.length) {
        this.chart.getDatasetMeta(hiddenCharts[i]).hidden = true;
      }
    }
  }
  close() {
    //console.log('close');
    this.removeAllChildNodes();
  }
  removeAllChildNodes() {
    while (this.parent.firstChild) {
      this.parent.removeChild(this.parent.firstChild);
    }
  }
  genereateData() {
    this.datasets = [];
    //Preparing y-axis data
    let yData = [];
    //console.log(this.harmonicsData.voltage.amplitude);
    if (this.harmonicsData.voltage.amplitude.length > 0) {
      for (let i = 0; i < this.harmonicsData.voltage.amplitude[0].length; i++) {
        yData[i] = [];
      }
    }
    //console.log(yData);
    for (let i = 0; i < this.harmonicsData.voltage.amplitude.length; i++) {
      for (let j = 0; j < this.harmonicsData.voltage.amplitude[i].length; j++) {
        yData[j][i] = this.harmonicsData.voltage.amplitude[i][j];
      }
    }
    for (let i = 0; i < yData.length; i++) {
      this.datasets.push({
        label: 'V_'+(i+1).toString(),
        data: yData[i],
        borderColor: phaseColors[i],
        backgroundColor: phaseColors[i],
        showLine: true,
        yAxisID: 'voltage',
      });
    }
    this.data = {
      labels: this.harmonicsData.frequenciesNumber,
      datasets: this.datasets,
    };
    //console.log(this.data);
  }
  generateConfi() {
    this.config = {
      type: 'bar',
      data: this.data,
      options: {
        maintainAspectRatio: false,
        animation: {
          duration: 0
        },
        elements: {
          point:{
           radius: 0
          }
        },
        responsive: true,
        interaction: {
          mode: 'index',
          intersect: false,
        },
        stacked: false,
        plugins: {
          title: {
            display: true,
            text: 'Displaying all the harmonics'
          }
        },
        scales: {
          x: {
            type: 'linear',
            display: true,
            position: 'bottom',
            title: {
              display: true,
              text: 'Harmonics multiples of fundamental frequency = '+(Math.round(this.harmonicsData.fundamentalFrequency)).toFixed(2)+' Hz'
            },
          },
          voltage: {
            type: 'linear',
            display: true,
            position: 'left',
            title: {
              display: true,
              text: 'Volt'
            },
          },
        }
      },
    };
  }
  prepareChartXTitle() {
    this.chart.options.scales.x.title.text = 'Harmonics multiples of fundamental frequency = '+(Math.round(this.harmonicsData.fundamentalFrequency)).toFixed(2)+' Hz';
  }
}