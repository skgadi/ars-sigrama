const { ipcMain } = require('electron')

const { SerialPort } = require('serialport')


let serialPortDevice = null;



function openSerialPort (serialPortID) {
  if (!serialPortID) {
    console.log('no serial port id');
    return;
  }
  if (!!serialPortDevice) {
    if (serialPortDevice.path === serialPortID && serialPortDevice.isOpen) {
      console.log('already open');
      return;
    } else {
      serialPortDevice.close();
      console.log('closed serial port');
    }
  }
  serialPortDevice = new SerialPort({
    path: serialPortID,
    baudRate: 115200
  });
  //console.log('opened serial port', serialPortDevice);
}


ipcMain.handle( 'serial-port', async ( event, data ) => {
  console.log('serial-port', data);
  if (data.c === 0) {
    return SerialPort.list();
  }
  if (data.c === 1) {
    //Open port if not open
    openSerialPort(data.path);
    await sleep(100);
    //Write the data to the serial port
    serialPortDevice.write(data.data);

    //Wait for the data to be written to the serial port
    //Close the serial port if the write operation is unsuccessful
    
    /*if (!serialPortDevice.drain()) {
      serialPortDevice.close();
    }*/
    //Wait for the data to be read from the serial port
    await sleep(50);
    //Read all the available data from the serial port in a buffer
    let buffer = [];
    let dataRead = serialPortDevice.read();
    while (dataRead != null) {
      await sleep(10);
      buffer.push(dataRead);
      dataRead = serialPortDevice.read();
    }
    //console.log(buffer);  
    return buffer;
  }
});



function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}
