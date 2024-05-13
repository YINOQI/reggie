var web_prefix = '/front'

function imgPath(path){
    return '/common/download?name=' + path
}
//将url传参转换为数组
function parseUrl(url) {
    // 找到url中的第一个?号
    var parse = url.substring(url.indexOf("?") + 1),
        params = parse.split("&"),
        len = params.length,
        item = [],
        param = {};

    for (var i = 0; i < len; i++) {
        item = params[i].split("=");
        param[item[0]] = item[1];
    }

    return param;
}
const util = {
    web_prefix,
    getUrlParam(name) {
        let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        let r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return decodeURI(r[2]);
        }
        return "";
    },
    formatPrice(val) {
        if (typeof val === 'string') {
            if (isNaN(val)) {
                return null;
            }
            // 价格转为整数
            const index = val.lastIndexOf(".");
            let p = "";
            if (index < 0) {
                // 无小数
                p = val + "00";
            } else if (index === p.length - 2) {
                // 1位小数
                p = val.replace("\.", "") + "0";
            } else {
                // 2位小数
                p = val.replace("\.", "")
            }
            return parseInt(p);
        } else if (typeof val === 'number') {
            if (!val) {
                return null;
            }
            const s = val + '';
            if (s.length === 0) {
                return "0.00";
            }
            if (s.length === 1) {
                return "0.0" + val;
            }
            if (s.length === 2) {
                return "0." + val;
            }
            const i = s.indexOf(".");
            if (i < 0) {
                return s.substring(0, s.length - 2) + "." + s.substring(s.length - 2)
            }
            const num = s.substring(0, i) + s.substring(i + 1);
            if (i === 1) {
                // 1位整数
                return "0.0" + num;
            }
            if (i === 2) {
                return "0." + num;
            }
            if (i > 2) {
                return num.substring(0, i - 2) + "." + num.substring(i - 2)
            }
        }
    }
}
