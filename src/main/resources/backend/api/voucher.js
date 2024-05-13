const addVoucher = (params) => {
    return $axios({
        url: '/voucher/addVoucher',
        method: 'put',
        data: {...params}
    })
}

const getVoucher = (params) => {
    return $axios({
        url: '/voucher/voucherList',
        method: 'get',
        params
    })
}

const deleteVoucher = (ids) => {
    return $axios({
        url: '/voucher',
        method: 'delete',
        params: {ids}
    })
}

const status = (params) => {
    return $axios({
        url: '/voucher/status',
        method: 'post',
        params
    })
}