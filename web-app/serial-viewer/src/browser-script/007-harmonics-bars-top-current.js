class harmonicsBarsTopCurrent {
  constructor(inDOM, inHarmonicsData, inBarCount) {

    this.barCount = inBarCount;

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
    this.chart.getDatasetMeta(3).hidden = true;
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
    //Find indexes of the harmonics with the highest amplitude in the first phase
    let indexes = [];
    let fundamentalFrequencyAmplitude = [];
    for (let i = 0; i < this.harmonicsData.voltage.amplitude.length; i++) {
      if (i== 1) {
        for (let j = 0; j < this.harmonicsData.voltage.amplitude[i].length; j++) {
          fundamentalFrequencyAmplitude.push(this.harmonicsData.voltage.amplitude[i][j]);
        }
        i++;
      }
      indexes.push(i);
    }
    indexes.sort((a, b) => this.harmonicsData.voltage.amplitude[b][0] - this.harmonicsData.voltage.amplitude[a][0]);
    indexes = indexes.slice(0, this.barCount);
    //Preparing y-axis data for the harmonics with the highest amplitude in the first phase
    let yData = [];
    if (this.harmonicsData.voltage.amplitude.length > 0) {
      for (let i = 0; i < this.harmonicsData.voltage.amplitude[0].length; i++) {
        yData[i] = [];
      }
    } else {
      return;
    }
    let labels = [];
    for (let i = 0; i < indexes.length; i++) {
      labels.push('H_'+(indexes[i]).toString());
      for (let j = 0; j < this.harmonicsData.voltage.amplitude[0].length; j++) {
        yData[j][i] = (this.harmonicsData.voltage.amplitude[indexes[i]][j])/fundamentalFrequencyAmplitude[j]*100;
      }
    }
    //console.log(yFullData);

    for (let i = 0; i < yData.length; i++) {
      this.datasets.push({
        label: 'V_'+(i+1).toString(),
        data: yData[i],
        borderColor: phaseColors[i],
        backgroundColor: phaseColors[i],
        showLine: true,
        yAxisID: 'voltage %',
      });
    }
    this.data = {
      labels: labels,
      datasets: this.datasets,
    };
    console.log(this.data);
  }
  generateConfi() {
    //console.log(this.harmonicsData.fundamentalFrequency);
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
            title: {
              display: true,
              text: 'Harmonics multiples of fundamental frequency = '+(Math.round(this.harmonicsData.fundamentalFrequency)).toFixed(2)+' Hz'
            }
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