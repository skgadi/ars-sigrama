class phasorDiagram {
  
  constructor(inCanvas, inHarmonics) {
    this.canvas = inCanvas;
    this.harmonics = inHarmonics;
    this.preparePolarChartBackground();
    this.plotHarmonics();
    this.placeCenterPin();
  }
  

  preparePolarChartBackground() {
    this.canvas.width = this.canvas.clientWidth;
    this.canvas.height = this.canvas.clientHeight;
    this.ctx = this.canvas.getContext("2d");
    let height = this.canvas.height;
    let width = this.canvas.width;
    //find center of canvas
    this.centerX = width / 2;
    this.centerY = height / 2;
    // The grafic will be square so take the shortest side
    let shortestSide = Math.min(width, height);
    //Generating basic constants for the chart
    this.lineSizeThickest = shortestSide/100;
    this.lineSizeThick = this.lineSizeThickest/2;
    this.lineSizeThin = this.lineSizeThickest/4;
    this.blankFillColor = '#fff';
    this.fullRadius = shortestSide / 2 - shortestSide*0.075;
    //Draw a circle at the center of the canvas
    this.ctx.beginPath();
    this.ctx.arc(this.centerX, this.centerY, this.fullRadius, 0, 2 * Math.PI, false);
    this.ctx.lineWidth = this.lineSizeThickest;
    //ctx.strokeStyle = '#000';
    this.ctx.stroke();
    this.ctx.closePath();
    this.ctx.beginPath();
    this.ctx.arc(this.centerX, this.centerY, 0.75*this.fullRadius, 0, 2 * Math.PI, false);
    this.ctx.lineWidth = this.lineSizeThin;
    this.ctx.stroke();
    this.ctx.beginPath();
    this.ctx.arc(this.centerX, this.centerY, 0.5*this.fullRadius, 0, 2 * Math.PI, false);
    this.ctx.lineWidth = this.lineSizeThick;
    this.ctx.stroke();
    this.ctx.beginPath();
    this.ctx.arc(this.centerX, this.centerY, 0.25*this.fullRadius, 0, 2 * Math.PI, false);
    this.ctx.lineWidth = this.lineSizeThin;
    this.ctx.stroke();
    this.ctx.closePath();
    // Draw lines from center to the circle
    // 0 degrees
    this.ctx.beginPath();
    this.ctx.moveTo(this.centerX - this.fullRadius, this.centerY);
    this.ctx.lineTo(this.centerX + this.fullRadius, this.centerY);
    this.ctx.lineWidth = this.lineSizeThick;
    this.ctx.stroke();
    this.ctx.closePath();
    // 90 degrees
    this.ctx.beginPath();
    this.ctx.moveTo(this.centerX, this.centerY - this.fullRadius);
    this.ctx.lineTo(this.centerX, this.centerY + this.fullRadius);
    this.ctx.lineWidth = this.lineSizeThick;
    this.ctx.stroke();
    this.ctx.closePath();
    // 30 degrees
    this.ctx.beginPath();
    this.ctx.moveTo(this.centerX - 0.866*this.fullRadius, this.centerY - 0.5*this.fullRadius);
    this.ctx.lineTo(this.centerX + 0.866*this.fullRadius, this.centerY + 0.5*this.fullRadius);
    this.ctx.lineWidth = this.lineSizeThin;
    this.ctx.stroke();
    this.ctx.closePath();
    // 60 degrees
    this.ctx.beginPath();
    this.ctx.moveTo(this.centerX - 0.5*this.fullRadius, this.centerY - 0.866*this.fullRadius);
    this.ctx.lineTo(this.centerX + 0.5*this.fullRadius, this.centerY + 0.866*this.fullRadius);
    this.ctx.lineWidth = this.lineSizeThin;
    this.ctx.stroke();
    this.ctx.closePath();
    // 120 degrees
    this.ctx.beginPath();
    this.ctx.moveTo(this.centerX - 0.5*this.fullRadius, this.centerY + 0.866*this.fullRadius);
    this.ctx.lineTo(this.centerX + 0.5*this.fullRadius, this.centerY - 0.866*this.fullRadius);
    this.ctx.lineWidth = this.lineSizeThin;
    this.ctx.stroke();
    this.ctx.closePath();
    // 150 degrees
    this.ctx.beginPath();
    this.ctx.moveTo(this.centerX - 0.866*this.fullRadius, this.centerY + 0.5*this.fullRadius);
    this.ctx.lineTo(this.centerX + 0.866*this.fullRadius, this.centerY - 0.5*this.fullRadius);
    this.ctx.lineWidth = this.lineSizeThin;
    this.ctx.stroke();
    this.ctx.closePath();
    // Show text on the circle (0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330)
    this.fontSize = shortestSide*0.03;
    this.font = this.fontSize + "px Arial";
    this.ctx.font = this.font;
    //this.ctx.fillStyle = "#000";
    this.ctx.textAlign = "center";
    this.ctx.textBaseline = "middle";
    let textRadius = this.fullRadius + shortestSide*0.03;
    this.ctx.fillText("0°", this.centerX + textRadius, this.centerY);
    this.ctx.fillText("30°", this.centerX + 0.866*textRadius, this.centerY - 0.5*textRadius);
    this.ctx.fillText("60°", this.centerX + 0.5*textRadius, this.centerY - 0.866*textRadius);
    this.ctx.fillText("90°", this.centerX, this.centerY - textRadius);
    this.ctx.fillText("120°", this.centerX - 0.5*textRadius, this.centerY - 0.866*textRadius);
    this.ctx.fillText("150°", this.centerX - 0.866*textRadius, this.centerY - 0.5*textRadius);
    this.ctx.fillText("180°", this.centerX - textRadius, this.centerY);
    this.ctx.fillText("210°", this.centerX - 0.866*textRadius, this.centerY + 0.5*textRadius);
    this.ctx.fillText("240°", this.centerX - 0.5*textRadius, this.centerY + 0.866*textRadius);
    this.ctx.fillText("270°", this.centerX, this.centerY + textRadius);
    this.ctx.fillText("300°", this.centerX + 0.5*textRadius, this.centerY + 0.866*textRadius);
    this.ctx.fillText("330°", this.centerX + 0.866*textRadius, this.centerY + 0.5*textRadius);
  
  }
  
  placeArrowOnPolarChart(angle, inRadius, color, arrowType = 0) {
    //console.log("angle", angle, "radius", inRadius, "color", color, "arrowType", arrowType);
    let radius = Math.abs(inRadius);
    let arrowHeadLength = this.fullRadius/12;
    let arrowHeadAngle = 20*Math.PI/180;
    let arrowLength =  radius - (arrowHeadLength*(Math.cos(arrowHeadAngle)));
    this.ctx.beginPath();
    this.ctx.moveTo(this.centerX, this.centerY);
    let x = this.centerX + arrowLength * Math.cos(angle);
    let y = this.centerY - arrowLength * Math.sin(angle);
    this.ctx.lineTo(x, y);
    this.ctx.lineWidth = this.lineSizeThick;
    this.ctx.strokeStyle = color;
    this.ctx.stroke();
    this.ctx.closePath();
    // Draw arrow head. It will be a triagle with the angle of arrowHeadAngle degrees
    // First point of the triangle is the end of the arrow
  
    this.ctx.beginPath();
    x = this.centerX + radius * Math.cos(angle);
    y = this.centerY - radius * Math.sin(angle);
    this.ctx.moveTo(x, y);
    let x1 = x - arrowHeadLength * Math.cos(angle + arrowHeadAngle);
    let y1 = y + arrowHeadLength * Math.sin(angle + arrowHeadAngle);
    this.ctx.lineTo(x1, y1);
    let x2 = x - arrowHeadLength * Math.cos(angle - arrowHeadAngle);
    let y2 = y + arrowHeadLength * Math.sin(angle - arrowHeadAngle);
    this.ctx.lineTo(x2, y2);
    this.ctx.lineTo(x, y);
    this.ctx.lineWidth = this.lineSizeThick;
    if (!!arrowType) {
      this.ctx.lineWidth = 0;
      this.ctx.fillStyle = color;
      this.ctx.fill();
    } else {
      this.ctx.fillStyle = this.blankFillColor;
      this.ctx.lineWidth = this.lineSizeThick;
      this.ctx.fill();
      this.ctx.stroke();

    }
    this.ctx.closePath();
  }

  placeCenterPin() {
    this.ctx.beginPath();
    this.ctx.arc(this.centerX, this.centerY, 5, 0, 2 * Math.PI, false);
    this.ctx.lineWidth = 1;
    this.ctx.fillStyle = '#000';
    this.ctx.fill();
    this.ctx.closePath();
  }

  close() {
    
  }

  plotHarmonics() {
    if (this.harmonics.voltage.amplitude.length !== this.harmonics.voltage.phase.length) {
      console.error('Voltage amplitude and phase arrays have different length');
      return;
    }
    if (this.harmonics.current.amplitude.length !== this.harmonics.current.phase.length) {
      console.error('Current amplitude and phase arrays have different length');
      return;
    }
    if (this.harmonics.voltage.amplitude.length !== this.harmonics.current.amplitude.length) {
      console.error('Voltage and current amplitude arrays have different length');
      return;
    }
    if (this.harmonics.voltage.amplitude.length < 2) {
      console.error('Voltage amplitude doesn\'t contain the fundament frequency');
      return;
    }
    if (this.harmonics.current.amplitude.length < 2) {
      console.error('Current amplitude doesn\'t contain the fundament frequency');
      return;
    }
    // Find the maximum value in the amplitude arrays
    let maxAmplitudeVoltage = 0;
    for (let i =0; i < this.harmonics.voltage.amplitude[1].length; i++) {
      if (maxAmplitudeVoltage < this.harmonics.voltage.amplitude[1][i]) {
        maxAmplitudeVoltage = this.harmonics.voltage.amplitude[1][i];
      }
    }
    maxAmplitudeVoltage = this.getNearestScaleValue(maxAmplitudeVoltage);
    let maxAmplitudeCurrent = 0;
    for (let i =0; i < this.harmonics.current.amplitude[1].length; i++) {
      if (maxAmplitudeCurrent < this.harmonics.current.amplitude[1][i]) {
        maxAmplitudeCurrent = this.harmonics.current.amplitude[1][i];
      }
    }
    maxAmplitudeCurrent = this.getNearestScaleValue(maxAmplitudeCurrent);
    //Plot voltages
    for (let i =0; i < this.harmonics.voltage.amplitude[1].length; i++) {
      let angle = this.harmonics.voltage.phase[1][i];
      let radius = this.fullRadius*this.harmonics.voltage.amplitude[1][i]/maxAmplitudeVoltage;
      this.placeArrowOnPolarChart(angle, radius, phaseColors[i], 0);
    }
    //Plot currents
    for (let i =0; i < this.harmonics.current.amplitude[1].length; i++) {
      let angle = this.harmonics.current.phase[1][i];
      let radius = this.fullRadius*this.harmonics.current.amplitude[1][i]/maxAmplitudeCurrent;
      this.placeArrowOnPolarChart(angle, radius, phaseColors[i], 1);
    }

    //Place Max voltage as text on top left corner as legend
    this.ctx.font = this.font;
    this.ctx.fillStyle = "#000";
    this.ctx.textAlign = "left";
    this.ctx.textBaseline = "top";
    this.ctx.fillText("Voltage", 10, 10);
    this.ctx.fillText("Scale: "+ maxAmplitudeVoltage + " V", 10, 10 + this.fontSize);
    for (let i =0; i < this.harmonics.voltage.amplitude[1].length; i++) {
      //Place a triangle with sharp on top before the Voltage text to indicate the arrow color and type
      this.ctx.beginPath();
      this.ctx.strokeStyle = phaseColors[i];
      this.ctx.fillStyle = this.blankFillColor;
      this.ctx.moveTo(10, 10 + (i+3)*this.fontSize - this.lineSizeThick);
      this.ctx.lineTo(10 + this.fontSize, 10 + (i+3)*this.fontSize - this.lineSizeThick);
      this.ctx.lineTo(10 + this.fontSize/2, 10 + (i+2)*this.fontSize + this.lineSizeThick);
      this.ctx.lineTo(10, 10 + (i+3)*this.fontSize - this.lineSizeThick);
      this.ctx.closePath();
      this.ctx.fill();
      this.ctx.stroke();


      //set text color to the same as the arrow color
      this.ctx.fillStyle = phaseColors[i];
      this.ctx.fillText("V" + (i+1) + " = " + Math.round(this.harmonics.voltage.amplitude[1][i]*10)/10 + " V", 10 + this.fontSize, 10 + (i+2)*this.fontSize);
    }


    //Place Max current as text on top right corner as legend
    this.ctx.font = this.font;
    this.ctx.fillStyle = "#000";
    this.ctx.textAlign = "right";
    this.ctx.textBaseline = "top";
    this.ctx.fillText("Current", this.canvas.width - 10, 10);
    this.ctx.fillText("Scale: "+ maxAmplitudeCurrent + " mA", this.canvas.width - 10, 10 + this.fontSize);
    for (let i =0; i < this.harmonics.current.amplitude[1].length; i++) {
      //Place a triangle with sharp on top before the Voltage text to indicate the arrow color and type
      this.ctx.beginPath();
      this.ctx.strokeStyle = phaseColors[i];
      this.ctx.fillStyle = phaseColors[i];
      this.ctx.moveTo(this.canvas.width - 10, 10 + (i+3)*this.fontSize - this.lineSizeThick);
      this.ctx.lineTo(this.canvas.width - 10 - this.fontSize, 10 + (i+3)*this.fontSize - this.lineSizeThick);
      this.ctx.lineTo(this.canvas.width - 10 - this.fontSize/2, 10 + (i+2)*this.fontSize + this.lineSizeThick);
      this.ctx.lineTo(this.canvas.width - 10, 10 + (i+3)*this.fontSize - this.lineSizeThick);
      this.ctx.closePath();
      this.ctx.fill();



      //set text color to the same as the arrow color
      this.ctx.fillStyle = phaseColors[i];
      this.ctx.fillText("I" + (i+1) + " = " + Math.round(this.harmonics.current.amplitude[1][i]*10)/10 + " mA", this.canvas.width - 10 - this.lineSizeThick - this.fontSize, 10 + (i+2)*this.fontSize);
    }
    
    //Place the fundamental frequency as text on the bottom right corner
    //The frequency should be rounded to 2 decimal places and the text should be showing 2 decimal places at all the times

    this.ctx.font = this.font;
    this.ctx.fillStyle = "#000";
    this.ctx.textAlign = "right";
    this.ctx.textBaseline = "bottom";
    this.ctx.fillText("F = " + (Math.round(this.harmonics.fundamentalFrequency*100)/100).toFixed(2) + " Hz", this.canvas.width - 10, this.canvas.height - 10);


  }

  /**
   * finds nearest value for the maximum scale value
   * If the input is less than 100, the result will be nearest (upper) 10s of the input
   * If the input is more than 100, the result will be nearest (upper) 50s of the input
   * @param {number} value
   * @returns {number}
   * 
   * */
  getNearestScaleValue(value) {
    if (value < 100) {
      return Math.ceil(value/10)*10;
    } else {
      return Math.ceil(value/50)*50;
    }
  }
}
