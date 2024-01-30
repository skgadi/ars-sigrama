class waveformDiagram {
  constructor(inDOM, waveformData) {

    this.parent = inDOM;
    //create a new canvas inside parent
    this.canvas = document.createElement('canvas');
    this.parent.appendChild(this.canvas);
    //set canvas size
    this.canvas.width = this.parent.clientWidth;
    this.canvas.height = this.parent.clientHeight;

    this.waveformData = waveformData;
    this.genereateData();
    this.generateConfi();

    this.chart = new Chart(this.canvas,this.config);
  }
  updateChart() {
    this.genereateData();
    this.generateConfi();
    this.chart.update();
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
    for (let i = 0; i < this.waveformData.voltage.length; i++) {
      this.datasets.push({
        label: 'V_'+(i+1).toString(),
        data: this.waveformData.voltage[i],
        borderColor: phaseColors[i],
        backgroundColor: phaseColors[i],
        yAxisID: 'voltage',
      });
    }
    for (let i = 0; i < this.waveformData.current.length; i++) {
      this.datasets.push({
        label: 'I_'+(i+1).toString(),
        data: this.waveformData.current[i],
        borderColor: phaseColors[i],
        backgroundColor: phaseColors[i],
        yAxisID: 'current',
        borderDash: [5, 5],
      });
    }

    this.data = {
      labels: this.waveformData.time,
      datasets: this.datasets,
    };
  }
  generateConfi() {
    this.config = {
      type: 'line',
      data: this.data,
      options: {
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
            text: 'Waveform Diagram'
          }
        },
        scales: {
          voltage: {
            type: 'linear',
            display: true,
            position: 'left',
            title: {
              display: true,
              text: 'Volt'
            },
          },
          current: {
            type: 'linear',
            display: true,
            position: 'right',
            title: {
              display: true,
              text: 'milli Ampere'
            },
            // grid line settings
            grid: {
              drawOnChartArea: false, // only want the grid lines for one axis to show up
            },
          },
        }
      },
    };
  }
}