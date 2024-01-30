const { contextBridge, ipcRenderer } = require('electron')


console.log('preload.js loaded');
contextBridge.exposeInMainWorld( 'api', {
    send: ( channel, data ) => ipcRenderer.invoke( channel, data ),
    handle: ( channel, callable, event, data ) => ipcRenderer.on( channel, callable( event, data ) )
} )