var ws = require('nodejs-websocket');
var server = ws.createServer(function(socket){
// 事件名称为text(读取字符串时，就叫做text)，读取客户端传来的字符串
　  var count = 1;
    socket.on('text', function(str) {
　　     // 在控制台输出前端传来的消息
        console.log(str);
        //向前端回复消息
        socket.sendText('服务器端收到客户端端发来的消息了！' + count++);
    });
}).listen(3000);
