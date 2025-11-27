const { ipcMain } = require('electron')

const { SerialPort } = require('serialport')


let serialPortDevice = null;



async function  openSerialPort (serialPortID) {
  try {
    if (!serialPortID) {
      console.log('no serial port id');
      return;
    }
    if (!!serialPortDevice) {
      if (serialPortDevice.path === serialPortID && serialPortDevice.isOpen) {
        console.log('already open');
        return;
      } else {
        console.log('closing serial port', serialPortDevice.path);
        await serialPortDevice.close();
        console.log('closed serial port');
      }
    }
    console.log('opening serial port', serialPortID);
      serialPortDevice = await new SerialPort({
        path: serialPortID,
        baudRate: 115200
      });
      //if error occurs, set serialPortDevice to null
      serialPortDevice.on('error', (error) => {
        console.log('error on serial port', error);
        serialPortDevice = null;
      });
    } catch (error) {
      console.log('error opening serial port', error);
      serialPortDevice = null;
    }
    //console.log('opened serial port', serialPortDevice);
}


ipcMain.handle( 'serial-port', async ( event, data ) => {
  try {
    console.log('serial-port', data);
    if (data.c === 0) {
      return SerialPort.list();
    }
    if (data.c === 1) {
      //Open port if not open
      await openSerialPort(data.path);
      await sleep(100);
      //Write the data to the serial port
      if (!serialPortDevice.isOpen) {
        return null;
      }
      serialPortDevice.write(data.data);
  
      //Wait for the data to be written to the serial port
      //Close the serial port if the write operation is unsuccessful
      
      /*if (!serialPortDevice.drain()) {
        serialPortDevice.close();
      }*/
      //Wait for the data to be read from the serial port
      await sleep(150);
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
  } catch (error) {
    console.log('error', error);
    return null;
  }
});



function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}
