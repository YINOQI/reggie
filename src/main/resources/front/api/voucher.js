function getVoucherApi() {
    return $axios({
        'url': '/voucher',
        'method': 'get'
    })
}

function seckill(id){
    return $axios({
        'url': '/voucher/' + id,
        'method': 'post',
    })
}