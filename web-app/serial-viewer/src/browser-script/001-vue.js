

const app = createApp({
  data() {
    return {
      menuItems: [
        "Phasor diagram",
        "Waveform",
        "Harmonics",
        "Power",
        "Energy",
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
      },
      selectedMenuItem: -1,
      menuPixelsOffset: 150,
      phasorDiagram: null,
      waveformDiagram: null,
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
      this.updateAllGraphs();
    },
    updateAllGraphs: function () {
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
      if (!!el) {
        el.style.width = (window.innerWidth - this.menuPixelsOffset) + 'px';
        el.style.height = window.innerHeight + 'px';
        if (!!this.waveformDiagram) {
          this.waveformDiagram.close();
        }
        this.waveformDiagram = new waveformDiagram (el, this.waveform);
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

