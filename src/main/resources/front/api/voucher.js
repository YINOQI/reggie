function getVoucherApi() {
    return $axios({
        'url': '/voucher',
        'method': 'get'
    })
}
function getUserVoucher() {
    return $axios({
        'url': '/voucher/user-voucher',
        'method': 'get'
    })
}


function getVoucherList() {
    return $axios({
        'url': '/voucher/voucherList',
        'method': 'get'
    })
}

function seckill(id){
    return $axios({
        'url': '/voucher/' + id,
        'method': 'post',
    })
}