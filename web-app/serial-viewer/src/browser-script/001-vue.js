

const app = createApp({
  data() {
    return {
      menuItems: [
        "Phasor diagram",
        "Waveform",
        "Harmonics Voltage (complete)",
        "Harmonics Voltage (top-10)",
        "Harmonics Voltage (table)",
        "Harmonics Current (complete)",
        "Harmonics Current (top-10)",
        "Harmonics Current (table)",
        "Power",
        "Settings"
      ],
      settings: {
        serial: {
          availablePorts: [],
          selectedPort: null,
        },
        updateGraphTimer: {
          id: null,
          interval: 2, // seconds
        },
      },
      waveform: {
        stepTime: -1,
        voltage: [[]],
        current: [[]],
        time: [],
      },
      harmonics: {
        fundamentalFrequency: -1,
        voltage: {
          amplitude: [[]],
          phase: [[]],
        },
        current: {
          amplitude: [[]],
          phase: [[]],
        },
        frequencies: [],
        frequenciesNumber:[],
      },
      selectedMenuItem: -1,
      menuPixelsOffset: 250,
      phasorDiagram: null,
      waveformDiagram: null,
      harmonicsDiagram: null,
      harmonicsDiagramTop: null,
      harmonicsDiagramCurrent: null,
      harmonicsDiagramTopCurrent: null,
    }
  },
  watch: {
    selectedMenuItem: async function (val) {
      if (val === 0) {
        await nextTick();
        this.resizeRequired();
        if (!!this.phasorDiagram) {
          this.phasorDiagram.close();
        }
        this.phasorDiagram = new phasorDiagram (document.getElementById("canvas-phasor-diagram"), this.harmonics);
      }
      if (val === 1) {
        await nextTick();
        this.resizeRequired();
        if (!!this.waveformDiagram) {
          this.waveformDiagram.close();
        }
        this.waveformDiagram = new waveformDiagram (document.getElementById("div-waveform-diagram"), this.waveform);
      }
      if (val === 2) {
        await nextTick();
        this.resizeRequired();
        if (!!this.harmonicsDiagram) {
          this.harmonicsDiagram.close();
        }
        this.harmonicsDiagram = new harmonicsBarsFull (document.getElementById("div-harmonics-voltage-histogram-complete"), this.harmonics);
      }
      if (val === 3) {
        await nextTick();
        this.resizeRequired();
        if (!!this.harmonicsDiagramTop) {
          this.harmonicsDiagramTop.close();
        }
        this.harmonicsDiagramTop = new harmonicsBarsTop (document.getElementById("div-harmonics-voltage-histogram-top"), this.harmonics, 10);
      }
      if (val === 5) {
        await nextTick();
        this.resizeRequired();
        if (!!this.harmonicsDiagramCurrent) {
          this.harmonicsDiagramCurrent.close();
        }
        this.harmonicsDiagramCurrent = new harmonicsBarsFullCurrent (document.getElementById("div-harmonics-current-histogram-complete"), this.harmonics);
      }
      if (val === 6) {
        await nextTick();
        this.resizeRequired();
        if (!!this.harmonicsDiagramTopCurrent) {
          this.harmonicsDiagramTopCurrent.close();
        }
        this.harmonicsDiagramTopCurrent = new harmonicsBarsTopCurrent (document.getElementById("div-harmonics-current-histogram-top"), this.harmonics, 10);
      }
    },
    'settings.updateGraphTimer.interval': {
      deep: true,
      handler: async function (newVal, oldVal) {
        this.updateTheDataFromTheMachine(); 
      }
    }
  },
  mounted() {
    console.log('mounted');
    this.refreshSerialPorts();
    this.updateTheDataFromTheMachine();
  },
  methods: {
    resizeRequired: function () {
      this.updateAllGraphs(true);
    },
    updateAllGraphs: function (callFromResize = false) {
      let el = document.getElementById('canvas-phasor-diagram');
      if (!!el) {
        el.style.width = (window.innerWidth - this.menuPixelsOffset) + 'px';
        el.style.height = window.innerHeight + 'px';
        if (!!this.phasorDiagram) {
          this.phasorDiagram.close();
        }
        this.phasorDiagram = new phasorDiagram (el, this.harmonics);
      }
      el = document.getElementById('div-waveform-diagram');
      //check if el contains child nodes
      if (!!el) {
        el.style.width = (window.innerWidth - this.menuPixelsOffset) + 'px';
        el.style.height = window.innerHeight + 'px';
        if (el.hasChildNodes()) {
          this.waveformDiagram.updateChart(this.waveform);
        } else {
          this.waveformDiagram = new waveformDiagram (el, this.waveform);
        }
      }
      el = document.getElementById('div-harmonics-voltage-histogram-complete');
      if (!!el) {
        el.style.width = (window.innerWidth - this.menuPixelsOffset) + 'px';
        el.style.height = window.innerHeight + 'px';
        if (el.hasChildNodes()) {
          this.harmonicsDiagram.updateChart(this.harmonics);
        } else {
          this.harmonicsDiagram = new harmonicsBarsFull (el, this.harmonics);
        }
      }
      el = document.getElementById('div-harmonics-voltage-histogram-top');
      if (!!el) {
        el.style.width = (window.innerWidth - this.menuPixelsOffset) + 'px';
        el.style.height = window.innerHeight + 'px';
        if (el.hasChildNodes()) {
          this.harmonicsDiagramTop.updateChart(this.harmonics);
        } else {
          this.harmonicsDiagramTop = new harmonicsBarsTop (el, this.harmonics, 10);
        }
      }



      el = document.getElementById('div-harmonics-current-histogram-complete');
      if (!!el) {
        el.style.width = (window.innerWidth - this.menuPixelsOffset) + 'px';
        el.style.height = window.innerHeight + 'px';
        if (el.hasChildNodes()) {
          this.harmonicsDiagramCurrent.updateChart(this.harmonics);
        } else {
          this.harmonicsDiagramCurrent = new harmonicsBarsFullCurrent (el, this.harmonics);
        }
      }
      el = document.getElementById('div-harmonics-current-histogram-top');
      if (!!el) {
        el.style.width = (window.innerWidth - this.menuPixelsOffset) + 'px';
        el.style.height = window.innerHeight + 'px';
        if (el.hasChildNodes()) {
          this.harmonicsDiagramTopCurrent.updateChart(this.harmonics);
        } else {
          this.harmonicsDiagramTopCurrent = new harmonicsBarsTopCurrent (el, this.harmonics, 10);
        }
      }

    },
    refreshSerialPorts: async function () {
      let list = await api.send('serial-port',{'c':0}); // 0 = list
      this.settings.serial['availablePorts'] = list;
      if (list.length > 0) {
        if (!this.settings.serial['selectedPort']) {
          this.settings.serial['selectedPort'] = list[0].path;
        }
      }
    },
    sendReceive: async function () {
      let data = await api.send('serial-port',{'c':1, 'path': this.settings.serial['selectedPort'], 'data': "3"});
      console.log("response: ", data);
    },
    updateTheDataFromTheMachine: async function () {
      if (!!this.settings.updateGraphTimer.id) {
        clearInterval(this.settings.updateGraphTimer.id);
      }
      this.settings.updateGraphTimer.id = setInterval(async () => {
        let data = await api.send('serial-port',{'c':1, 'path': this.settings.serial['selectedPort'], 'data': "3"});
        //console.log("response: ", data);
        if (data.length > 0 && data[0].length === 8200) {
          this.processInputFromMachine(data[0].buffer);
          this.updateAllGraphs();
        } else {
          console.log ('unexpected response');
        }
      }, this.settings.updateGraphTimer.interval*1000);
    },
    processInputFromMachine: function (bufferData) {
      this.waveform.voltage = [];
      this.waveform.current = [];
      this.harmonics.voltage.amplitude = [[]];
      this.harmonics.voltage.phase = [[]];
      this.harmonics.current.amplitude = [[]];
      this.harmonics.current.phase = [[]];
      this.waveform.time = [];
      this.harmonics.frequencies = [];
      this.harmonics.frequenciesNumber = [];
      const RESAMPLE_SIZE = 128;
      const NO_OF_CHANNELS = 4; // 4 for voltage and 4 for current
      let idx = 0;
      //Convert byte array to float
      const view = new DataView(bufferData);
      this.harmonics.fundamentalFrequency = view.getFloat32(idx, true); idx += 4;
      this.waveform.stepTime = view.getFloat32(idx, true); idx += 4;
      //fill the time array of size RESAMPLE_SIZE with values from 0 with step of stepTime
      for (let i=0; i<RESAMPLE_SIZE; i++) {
        this.waveform.time[i] = i*this.waveform.stepTime;
      }
      //fill the frequency array of size RESAMPLE_SIZE/2 with values from 0 with step of fundamentalFrequency
      for (let i=0; i<RESAMPLE_SIZE/2; i++) {
        this.harmonics.frequencies[i] = i*this.harmonics.fundamentalFrequency;
        this.harmonics.frequenciesNumber[i] = i;
      }

      for (let i=0; i<NO_OF_CHANNELS; i++) {
        this.waveform.voltage[i] = [];
        this.waveform.current[i] = [];
      }
      for (let i=0; i<RESAMPLE_SIZE; i++) {
        for (let j=0; j<NO_OF_CHANNELS; j++) {
          this.waveform.voltage[j][i] = view.getFloat32(idx, true); idx += 4;
        }
        for (let j=0; j<NO_OF_CHANNELS; j++) {
          this.waveform.current[j][i] = view.getFloat32(idx, true); idx += 4;
        }
      }
      for (let i=0; i<RESAMPLE_SIZE/2; i++) {
        this.harmonics.voltage.amplitude[i] = [];
        for (let j=0; j<NO_OF_CHANNELS; j++) {
          this.harmonics.voltage.amplitude[i][j] = view.getFloat32(idx, true); idx += 4;
        }
        this.harmonics.current.amplitude[i] = [];
        for (let j=0; j<NO_OF_CHANNELS; j++) {
          this.harmonics.current.amplitude[i][j] = view.getFloat32(idx, true); idx += 4;
        }
      }
      for (let i=0; i<RESAMPLE_SIZE/2; i++) {
        this.harmonics.voltage.phase[i] = [];
        for (let j=0; j<NO_OF_CHANNELS; j++) {
          this.harmonics.voltage.phase[i][j] = view.getFloat32(idx, true); idx += 4;
        }
        this.harmonics.current.phase[i] = [];
        for (let j=0; j<NO_OF_CHANNELS; j++) {
          this.harmonics.current.phase[i][j] = view.getFloat32(idx, true); idx += 4;
        }
      }
      //Print first 3 harmonics
      for (let i=0; i<3; i++) {
        //console.log('voltage harmonic:', i, this.harmonics.voltage.amplitude[i], this.harmonics.voltage.phase[i]);
        //console.log('current harmonic:', i, this.harmonics.current.amplitude[i], this.harmonics.current.phase[i]);
      }
      //console.log(this.waveform);
      //console.log(this.harmonics);

    }
  }
}).mount('#app')

