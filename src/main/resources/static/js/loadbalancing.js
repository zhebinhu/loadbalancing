//集群规模
var clusterSize = 5;
// 基于准备好的dom，初始化echarts实例
var charts = [];
var hitCharts = [];

for (var i = 0; i < clusterSize; i++) {
    charts.push(echarts.init(document.getElementById('chart' + i + '0'), null, {renderer: 'svg'}));
}
for (var i = 0; i < clusterSize; i++) {
    hitCharts.push(echarts.init(document.getElementById('chart' + i + '1'), null, {renderer: 'svg'}));
}
ave_chart = echarts.init(document.getElementById('ave_chart'), null, {renderer: 'svg'});
ave_hit_chart = echarts.init(document.getElementById('ave_hit_chart'), null, {renderer: 'svg'});
variance_chart = echarts.init(document.getElementById('variance_chart'), null, {renderer: 'svg'});

//变量声明
{
    var time = [];
    var consistLoad = [];
    var consistLoadArray = [];
    var normalLoad = [];
    var normalLoadArray = [];
    var advConsistLoad = [];
    var advConsistLoadArray = [];
    var consistHitRatio = [];
    var consistHitRatioArray = [];
    var normalHitRatio = [];
    var normalHitRatioArray = [];
    var advConsistHitRatio = [];
    var advConsistHitRatioArray = [];
    var isRunning = false;
    var now = 0.0;
    var aveConsistLoad = [];
    var aveNormalLoad = [];
    var aveAdvConsistLoad = [];
    var aveConsistHitRatio = [];
    var aveNormalHitRatio = [];
    var aveAdvConsistHitRatio = [];
    var consistLoadVariance = [];
    var normalVariance = [];
    var advConsistLoadVariance = [];

//同时显示的数据个数
    var data_length = 100;
//更新间隔
    var update_time = 0.1;

}

//变量初始化
{
    for (var i = 0; i < clusterSize; i++) {
        consistLoadArray.push([]);
        normalLoadArray.push([]);
        advConsistLoadArray.push([]);
        consistHitRatioArray.push([]);
        normalHitRatioArray.push([]);
        advConsistHitRatioArray.push([]);
    }

    var a = 0.0;
    for (var i = 0; i < data_length; i++) {
        time.push(a.toFixed(1));
        a = a + update_time;
    }
}

//按钮动作设置
{
    document.getElementById('stop').onclick = function (ev) {
        isRunning = false;
    }
    document.getElementById('start').onclick = function (ev) {
        isRunning = true;
    }
    document.getElementById('remove0').onclick = function (ev) {
        setNodeAjax('GET', 'http://127.0.0.1:8080/removeNode', "nodeNum=0");
    }
    document.getElementById('add0').onclick = function (ev) {
        setNodeAjax('GET', 'http://127.0.0.1:8080/addNode', "nodeNum=0");
    }
    document.getElementById('remove1').onclick = function (ev) {
        setNodeAjax('GET', 'http://127.0.0.1:8080/removeNode', "nodeNum=1");
    }
    document.getElementById('add1').onclick = function (ev) {
        setNodeAjax('GET', 'http://127.0.0.1:8080/addNode', "nodeNum=1");
    }
    document.getElementById('remove2').onclick = function (ev) {
        setNodeAjax('GET', 'http://127.0.0.1:8080/removeNode', "nodeNum=2");
    }
    document.getElementById('add2').onclick = function (ev) {
        setNodeAjax('GET', 'http://127.0.0.1:8080/addNode', "nodeNum=2");
    }
    document.getElementById('remove3').onclick = function (ev) {
        setNodeAjax('GET', 'http://127.0.0.1:8080/removeNode', "nodeNum=3");
    }
    document.getElementById('add3').onclick = function (ev) {
        setNodeAjax('GET', 'http://127.0.0.1:8080/addNode', "nodeNum=3");
    }
    document.getElementById('remove4').onclick = function (ev) {
        setNodeAjax('GET', 'http://127.0.0.1:8080/removeNode', "nodeNum=4");
    }
    document.getElementById('add4').onclick = function (ev) {
        setNodeAjax('GET', 'http://127.0.0.1:8080/addNode', "nodeNum=4");
    }
}

//配置设置
{
    var loadOption = [];
    var hitOption = [];
    aveLoadOption = {
        title: {
            text: '服务器平均负载情况'
        },
        xAxis: {
            type: 'category',
            name: '时间/s',
            data: time
        },
        yAxis: {
            type: 'value',
            name: '负载',
            boundaryGap: [0, '50%']
        },
        series: [
            {
                name: '一致性哈希环',
                type: 'line',
                smooth: true,
                lineStyle: {
                    color: '#ababff'
                },
                areaStyle: {
                    opacity: 0,
                    color: '#ababff'
                },
                data: aveConsistLoad
            },
            {
                name: '普通哈希',
                type: 'line',
                smooth: true,
                lineStyle: {
                    color: '#ff3219'
                },
                areaStyle: {
                    opacity: 0,
                    color: '#ff3219'
                },
                data: aveNormalLoad
            },
            {
                name: '改进一致性哈希',
                type: 'line',
                smooth: true,
                lineStyle: {
                    color: '#09ff05'
                },
                areaStyle: {
                    opacity: 0,
                    color: '#09ff05'
                },
                data: aveAdvConsistLoad
            }
        ]
    };
    aveHitOption = {
        title: {
            text: '服务器平均命中情况'
        },
        xAxis: {
            type: 'category',
            name: '时间/s',
            data: time
        },
        yAxis: {
            type: 'value',
            name: '命中率/%',
            max: 100
        },
        series: [
            {
                name: '普通哈希',
                smooth: true,
                type: 'line',
                lineStyle: {
                    color: '#ff3219'
                },
                areaStyle: {
                    opacity: 0,
                    color: '#ff3219'
                },
                data: aveNormalHitRatio
            },
            {
                name: '一致性哈希环',
                smooth: true,
                type: 'line',
                lineStyle: {
                    color: '#ababff'
                },
                areaStyle: {
                    opacity: 0,
                    color: '#ababff'
                },
                data: aveConsistHitRatio
            },
            {
                name: '改进一致性哈希',
                type: 'line',
                smooth: true,
                lineStyle: {
                    color: '#09ff05'
                },
                areaStyle: {
                    opacity: 0,
                    color: '#09ff05'
                },
                data: aveAdvConsistHitRatio
            }
        ]
    };
    varianceOption = {
        title: {
            text: '服务器负载方差'
        },
        xAxis: {
            type: 'category',
            name: '时间/s',
            data: time
        },
        yAxis: {
            type: 'value',
            name: '方差',
        },
        series: [
            {
                name: '普通哈希',
                smooth: true,
                type: 'line',
                lineStyle: {
                    color: '#ff3219'
                },
                areaStyle: {
                    opacity: 0,
                    color: '#ff3219'
                },
                data: normalVariance
            },
            {
                name: '一致性哈希环',
                smooth: true,
                type: 'line',
                lineStyle: {
                    color: '#ababff'
                },
                areaStyle: {
                    opacity: 0,
                    color: '#ababff'
                },
                data: consistLoadVariance
            },
            {
                name: '改进一致性哈希',
                type: 'line',
                smooth: true,
                lineStyle: {
                    color: '#09ff05'
                },
                areaStyle: {
                    opacity: 0,
                    color: '#09ff05'
                },
                data: advConsistLoadVariance
            }
        ]
    };
    for (var i = 0; i < clusterSize; i++) {
        loadOption.push({
            title: {
                text: '服务器' + i + '负载情况'
            },
            xAxis: {
                type: 'category',
                name: '时间/s',
                data: time
            },
            yAxis: {
                type: 'value',
                name: '负载',
                boundaryGap: [0, '50%'],
                max: 50
            },
            series: [
                {
                    name: '一致性哈希环',
                    smooth: true,
                    type: 'line',
                    lineStyle: {
                        color: '#ababff'
                    },
                    areaStyle: {
                        opacity: 0,
                        color: '#ababff'
                    },
                    data: consistLoadArray[i]
                },
                {
                    name: '普通哈希',
                    smooth: true,
                    type: 'line',
                    lineStyle: {
                        color: '#ff3219'
                    },
                    areaStyle: {
                        opacity: 0,
                        color: '#ff3219'
                    },
                    data: normalLoadArray[i]
                },
                {
                    name: '改进一致性哈希',
                    type: 'line',
                    smooth: true,
                    lineStyle: {
                        color: '#09ff05'
                    },
                    areaStyle: {
                        opacity: 0,
                        color: '#09ff05'
                    },
                    data: advConsistLoadArray[i]
                }
            ]
        });
        hitOption.push({
            title: {
                text: '服务器' + i + '命中情况'
            },
            xAxis: {
                type: 'category',
                name: '时间/s',
                data: time
            },
            yAxis: {
                type: 'value',
                name: '命中率/%',
                max: 100
            },
            series: [
                {
                    name: '一致性哈希环',
                    smooth: true,
                    type: 'line',
                    lineStyle: {
                        color: '#ababff'
                    },
                    areaStyle: {
                        opacity: 0,
                        color: '#ababff'
                    },
                    data: consistHitRatioArray[i]
                },
                {
                    name: '普通哈希',
                    smooth: true,
                    type: 'line',
                    lineStyle: {
                        color: '#ff3219'
                    },
                    areaStyle: {
                        opacity: 0,
                        color: '#ff3219'
                    },
                    data: normalHitRatioArray[i]
                },
                {
                    name: '改进一致性哈希',
                    type: 'line',
                    smooth: true,
                    lineStyle: {
                        color: '#09ff05'
                    },
                    areaStyle: {
                        opacity: 0,
                        color: '#09ff05'
                    },
                    data: advConsistHitRatioArray[i]
                }
            ]
        });
    }
}

//心跳包
setInterval(function () {
    if (isRunning) {
        getLoadAjax('GET', 'http://127.0.0.1:8080/getLoad');
        getHitRatioAjax('GET', 'http://127.0.0.1:8080/getHitRatio');
    }
}, update_time * 1000);

//更新数据
setInterval(function () {

    if (isRunning) {
        var totalConsistLoad = 0;
        var totalConsistHit = 0;
        var totalNormalLoad = 0;
        var totalNormalHit = 0;
        var totalAdvConsistLoad = 0;
        var totalAdvConsistHit = 0;
        var actualClusterSize = clusterSize;
        for (var i = 0; i < clusterSize; i++) {
            if(consistLoad[i]<=0){
                actualClusterSize -= 1;
            }
            consistLoadArray[i].push(consistLoad[i]);
            normalLoadArray[i].push(normalLoad[i]);
            advConsistLoadArray[i].push(advConsistLoad[i]);
            consistHitRatioArray[i].push(consistHitRatio[i]);
            normalHitRatioArray[i].push(normalHitRatio[i]);
            advConsistHitRatioArray[i].push(advConsistHitRatio[i]);
            if (consistLoadArray[i].length > data_length) {
                consistLoadArray[i].shift();
            }
            if (normalLoadArray[i].length > data_length) {
                normalLoadArray[i].shift();
            }
            if (advConsistLoadArray[i].length > data_length) {
                advConsistLoadArray[i].shift();
            }
            if (consistHitRatioArray[i].length > data_length) {
                consistHitRatioArray[i].shift();
            }
            if (normalHitRatioArray[i].length > data_length) {
                normalHitRatioArray[i].shift();
            }
            if (advConsistHitRatioArray[i].length > data_length) {
                advConsistHitRatioArray[i].shift();
            }
            totalConsistLoad += consistLoad[i];
            totalConsistHit += consistHitRatio[i];
            totalNormalLoad += normalLoad[i];
            totalNormalHit += normalHitRatio[i];
            totalAdvConsistLoad += advConsistLoad[i];
            totalAdvConsistHit += advConsistHitRatio[i];
        }

        aveConsistLoad.push((totalConsistLoad / actualClusterSize).toFixed(2));
        aveNormalLoad.push((totalNormalLoad / actualClusterSize).toFixed(2));
        aveAdvConsistLoad.push((totalAdvConsistLoad / actualClusterSize).toFixed(2));
        aveConsistHitRatio.push((totalConsistHit / actualClusterSize).toFixed(2));
        aveNormalHitRatio.push((totalNormalHit / actualClusterSize).toFixed(2));
        aveAdvConsistHitRatio.push((totalAdvConsistHit / actualClusterSize).toFixed(2));

        if (aveConsistLoad.length > data_length) {
            aveConsistLoad.shift();
        }
        if (aveNormalLoad.length > data_length) {
            aveNormalLoad.shift();
        }
        if (aveAdvConsistLoad.length > data_length) {
            aveAdvConsistLoad.shift();
        }
        if (aveConsistHitRatio.length > data_length) {
            aveConsistHitRatio.shift();
        }
        if (aveNormalHitRatio.length > data_length) {
            aveNormalHitRatio.shift();
        }
        if (aveAdvConsistHitRatio.length > data_length) {
            aveAdvConsistHitRatio.shift();
        }

        now = now + update_time;

        if (now >= (data_length * update_time)) {
            time.push(now.toFixed(1));
            if (time.length > data_length) {
                time.shift();
            }
        }

        ave_chart.setOption(aveLoadOption);
        ave_hit_chart.setOption(aveHitOption);
        variance_chart.setOption(varianceOption);

        for (var i = 0; i < charts.length; i++) {
            charts[i].setOption(loadOption[i]);
        }

        for (var i = 0; i < hitCharts.length; i++) {
            hitCharts[i].setOption(hitOption[i]);
        }

    }
}, update_time * 1000);
//初始化图表
{
    for (var i = 0; i < charts.length; i++) {
        charts[i].setOption(loadOption[i]);
    }
    for (var i = 0; i < hitCharts.length; i++) {
        hitCharts[i].setOption(hitOption[i]);
    }
    ave_chart.setOption(aveLoadOption);
    ave_hit_chart.setOption(aveHitOption);
    variance_chart.setOption(varianceOption);
}

//获取服务器负载ajax
function getLoadAjax(type, url, param) {
    var xhr = new XMLHttpRequest(); //新建连接
    xhr.withCredentials = true;
    xhr.crossDomain = true;
    xhr.onreadystatechange = function () { //监听连接
        if (xhr.readyState === 4 && xhr.status === 200) {
            //do something
            reponse = JSON.parse(xhr.responseText);
            var aveConsistLoad = 0;
            var aveNormalLoad = 0;
            var aveAdvConsistLoad = 0;
            var actualClusterSize = clusterSize;
            for (var i = 0; i < clusterSize; i++) {
                consistLoad[i] = reponse[i];
                normalLoad[i] = reponse[i + clusterSize];
                advConsistLoad[i] = reponse[i + clusterSize * 2];
                if(reponse[i]>0) {
                    aveConsistLoad += reponse[i];
                    aveNormalLoad += reponse[i + clusterSize];
                    aveAdvConsistLoad += reponse[i + clusterSize * 2];
                }
                else {
                    actualClusterSize -= 1;
                }
            }
            aveConsistLoad /= actualClusterSize;
            aveNormalLoad /= actualClusterSize;
            aveAdvConsistLoad /= actualClusterSize;
            var a = 0;
            var b = 0;
            var c = 0;
            for (var i = 0; i < actualClusterSize; i++) {
                if(reponse[i]>0) {
                    a += (consistLoad[i] - aveConsistLoad) * (consistLoad[i] - aveConsistLoad);
                    b += (normalLoad[i] - aveNormalLoad) * (normalLoad[i] - aveNormalLoad);
                    c += (advConsistLoad[i] - aveAdvConsistLoad) * (advConsistLoad[i] - aveAdvConsistLoad);
                }
            }
            consistLoadVariance.push(a);
            normalVariance.push(b);
            advConsistLoadVariance.push(c);
            if(consistLoadVariance.length>data_length){
                consistLoadVariance.shift();
            }
            if(normalVariance.length>data_length){
                normalVariance.shift();
            }
            if(advConsistLoadVariance.length>data_length){
                advConsistLoadVariance.shift();
            }
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

//获取服务器命中率ajax
function getHitRatioAjax(type, url, param) {
    var xhr = new XMLHttpRequest(); //新建连接
    xhr.withCredentials = true;
    xhr.crossDomain = true;
    xhr.onreadystatechange = function () { //监听连接
        if (xhr.readyState === 4 && xhr.status === 200) {
            //do something
            reponse = JSON.parse(xhr.responseText);
            for (var i = 0; i < clusterSize; i++) {
                consistHitRatio[i] = reponse[i];
                normalHitRatio[i] = reponse[i + clusterSize];
                advConsistHitRatio[i] = reponse[i + clusterSize * 2];
            }
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

//设置节点ajax
function setNodeAjax(type, url, param) {
    var xhr = new XMLHttpRequest(); //新建连接
    xhr.withCredentials = true;
    xhr.crossDomain = true;
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
