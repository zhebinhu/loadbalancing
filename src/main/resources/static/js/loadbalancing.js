//集群规模
var clusterSize = 5;
// 基于准备好的dom，初始化echarts实例
var charts = [];

for (var i = 0; i < clusterSize; i++) {
    charts.push(echarts.init(document.getElementById('chart' + i)));
}
ave_chart = echarts.init(document.getElementById('ave_chart'));

// 指定图表的配置项和数据
var option = [];
var time = [];
var clusterlist = [];
var clusterload = [];
var isRunning = true;
var now = 0;
var ave_clusterload = [];

//同时显示的数据个数
var data_length = 300;
//更新间隔
var update_time = 0.1;

for (var i = 0; i < clusterSize; i++) {
    clusterlist.push([]);
    for (var j = 0; j < data_length; j++) {
        clusterload.push(0);
    }
}

var a = 0.0;
for (var i = 0; i < data_length; i++) {
    time.push(a.toFixed(1));
    a = a + update_time;
}
ave_option = {
    title: {
        text: '服务器平均负载情况'
    },
    xAxis: {
        name: '时间/s',
        type: 'category',
        boundaryGap: false,
        data: time
    },
    yAxis: {
        name: '负载',
        boundaryGap: [0, '50%'],
        type: 'value'
    },
    series: [
        {
            type: 'line',
            smooth: false,
            symbol: 'none',
            stack: 'a',
            lineStyle: {
                color: '#ababff'
            },
            areaStyle: {
                opacity: 0.2,
                color: '#ababff'
            },
            data: ave_clusterload
        }
    ]
}
for (var i = 0; i < clusterSize; i++) {
    option.push({
        title: {
            text: '服务器' + i + '负载情况'
        },
        xAxis: {
            name: '时间/s',
            type: 'category',
            boundaryGap: false,
            data: time
        },
        yAxis: {
            name: '负载',
            boundaryGap: [0, '50%'],
            type: 'value',
        },
        dataZoom: [
            {
                id: 'dataZoomY',
                type: 'inside',
                yAxisIndex: [0],
                filterMode: 'empty',
            }
        ],
        series: [
            {
                type: 'line',
                smooth: false,
                symbol: 'none',
                stack: 'a',
                lineStyle: {
                    color: '#ababff'
                },
                areaStyle: {
                    opacity: 0.2,
                    color: '#ababff'
                },
                data: clusterlist[i]
            }
        ]
    });
}
document.getElementById('stop').onclick = function (ev) {
    isRunning = false;
}
document.getElementById('start').onclick = function (ev) {
    isRunning = true;
}

// setInterval(function () {
//
//     if (isRunning) {
//         getServerNumAjax('GET', 'http://127.0.0.1:8080/getServerNum', 'data=' + Math.floor(Math.random() * 1024));
//     }
// }, update_time * 1000);

setInterval(function () {

    if (isRunning) {
        getLoadAjax('GET', 'http://127.0.0.1:8080/getLoad');
    }

}, update_time * 1000);

setInterval(function () {

    if (isRunning) {
        //总负载数
        var total_load = 0;
        for (var i = 0; i < clusterSize; i++) {
            clusterlist[i].push(clusterload[i]);
            if (clusterlist[i].length > data_length) {
                clusterlist[i].shift();
            }
            total_load += clusterload[i];
        }
        ave_clusterload.push((total_load / clusterSize).toFixed(2));
        if (ave_clusterload.length > data_length) {
            ave_clusterload.shift();
        }
        now = now + update_time;
        if (now >= (data_length * update_time)) {
            time.push(now.toFixed(1));
            if (time.length > data_length) {
                time.shift();
            }
        }
        ave_chart.setOption(ave_option);
        for (var i = 0; i < charts.length; i++) {
            charts[i].setOption({
                xAxis: {
                    data: time
                },
                series: [{
                    data: clusterlist[i]
                }]
            });
        }
    }
}, update_time * 1000);

// 使用刚指定的配置项和数据显示图表。
for (var i = 0; i < charts.length; i++) {
    charts[i].setOption(option[i]);
}

ave_chart.setOption(ave_option);


//如何实时更新图表?
// 在图表初始化后不管任何时候只要获取数据后通过 setOption 填入数据和配置项就行。

/**
 * ajax函数
 * @param  {String}   type     GET  /  POST
 * @param  {String}   url      基本网址如 www.baidu.com/tiebu
 * @param  {String}   param    请求参数 如   price=1&name=py
 * @return {Null}            无返回值
 */
// function getServerNumAjax(type, url, param) {
//     var xhr = new XMLHttpRequest(); //新建连接
//     xhr.withCredentials = true;
//     xhr.crossDomain = true;
//     // xhr.onreadystatechange = function () { //监听连接
//     //     if (xhr.readyState === 4 && xhr.status === 200) {
//     //         //do something
//     //         // reponse = JSON.parse(xhr.responseText);
//     //         // clusterload[reponse.ServerNum]++;
//     //         // // getDataNumAjax('GET', 'http://127.0.0.1:8080/getData', 'data=' + reponse.Data);
//     //     } else {
//     //     }
//     // }
//     var reg = /\?$/;
//     if (type === 'GET') { //GET方法
//         xhr.open('GET', reg.test(url) ? url + param : url + '?' + param, true);
//         xhr.send();
//     } else if (type === 'POST') { //POST方法
//         xhr.open('POST', url, true);
//         xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
//         xhr.send(param);
//     }
// }

function getLoadAjax(type, url, param) {
    var xhr = new XMLHttpRequest(); //新建连接
    xhr.withCredentials = true;
    xhr.crossDomain = true;
    xhr.onreadystatechange = function () { //监听连接
        if (xhr.readyState === 4 && xhr.status === 200) {
            //do something
            reponse = JSON.parse(xhr.responseText);
            for (var i = 0; i < clusterSize; i++) {
                clusterload[i] = reponse[i];
            }
            // clusterload[reponse.ServerNum]++;
            // // getDataNumAjax('GET', 'http://127.0.0.1:8080/getData', 'data=' + reponse.Data);
        } else {
        }
    }
    var reg = /\?$/;
    if (type === 'GET') { //GET方法
        xhr.open('GET', reg.test(url) ? url + param : url + '?' + param, true);
        xhr.send();
    } else if (type === 'POST') { //POST方法
        xhr.open('POST', url, true);
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xhr.send(param);
    }
}

// function getDataNumAjax(type, url, param) {
//     var xhr = new XMLHttpRequest(); //新建连接
//     xhr.withCredentials = true;
//     xhr.crossDomain = true;
//     xhr.onreadystatechange = function () { //监听连接
//         if (xhr.readyState === 4 && xhr.status === 200 && isRunning) {
//             //do something
//             reponse = JSON.parse(xhr.responseText);
//             clusterload[reponse.ServerNum]--;
//         } else {
//         }
//     }
//     var reg = /\?$/;
//     if (type === 'GET') { //GET方法
//         xhr.open('GET', reg.test(url) ? url + param : url + '?' + param, true);
//         xhr.send();
//     } else if (type === 'POST') { //POST方法
//         xhr.open('POST', url, true);
//         xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
//         xhr.send(param);
//     }
// }