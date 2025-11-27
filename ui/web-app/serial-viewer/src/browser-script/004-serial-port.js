async function serialPortList () {
  return api.send('serial-port',{'c':0}); // 0 = list
}