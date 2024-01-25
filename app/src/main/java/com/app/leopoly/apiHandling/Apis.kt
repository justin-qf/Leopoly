package com.app.leopoly.apiHandling

object Apis {
    const val LIVE = "http://192.168.1.16/leopoly/api/"
    private const val DEVELOPMENT = "http://192.168.1.13/leopoly/api/"
    const val DEVELOPMENT_LIVE_URL = "/leopoly/public/api/"
    const val DEVELOPMENT_DEBUG_URL = "/leopoly/api/"
    const val LOGIN = "login" //EMAIL=admin,PASSWORD=admin
    const val PARTY_LIST = "party_list" //
    const val SALES_PARTY_LIST = "sales_order/party_list"
    const val ITEM_LIST = "sales_order/items_list"
    const val TRANSPORT_LIST = "sales_order/transpotor_list"
    const val ORDER_LIST = "salesOrder_list" //party_id
    const val PACKAGE_SLIP_LIST =
        "packing_slip" //company_id, ac_year_id , sales_ord_no , party_id , inv_item_id
    const val ADD_SALES_ORDER = "store_order"
    const val ADD_SALES_ORDER_DETAIL = "sales_order/store_sales_order"
    const val PACKING_DETAIL = "packing_details" //barcode_scan_id
    const val ADD_PACKING_DETAIL = "store_package_info"
    const val DELETE_PACKAGE_BY_ID = "delete_package_by_id" //INVITID [PASS FOR DELETE]
    const val PACKAGE_DETAIL_LIST =
        "packing_details_by_order" //PASS ORDER ID FROM STORE ORDER = [INVID]
    const val DELETE_ORDER_BY_ID = "delete_order_by_id" //PASS ORDER ID FROM STORE ORDER = [ORDERID]
    const val GET_SALES_ORDER_NO = "sales_order/get_sales_order_no" //PASS compId AND accId
    const val GET_LESS_WT = "sales_order/get_less_wt" //PASS itemId AND partyId
    const val PACKAGE_SLIP_DETAIL_LIST = "order_get" //PASS company_id AND ac_year_id with inv_date
}