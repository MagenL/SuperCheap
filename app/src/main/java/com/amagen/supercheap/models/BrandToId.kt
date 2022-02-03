package com.amagen.supercheap.models

enum class BrandToId(val brandId:Int,val brandName:String?="", val iconURL:String, val priceBaseURL:String="", val storeIdBaseURL:String?="") {
    SHUFERSAL(
        brandId = 1,
        brandName = "שופרסל",
        iconURL = "https://res.cloudinary.com/shufersal/image/upload/f_auto,q_auto/v1551800922/prod/cmscontent/hde/h73/9038553808926",
        priceBaseURL = "http://prices.shufersal.co.il/FileObject/UpdateCategory?catID=2&page=",
        storeIdBaseURL = "http://prices.shufersal.co.il/FileObject/UpdateCategory?catID=2&storeId="

    ),
    VICTORY(
        brandId=2,
        brandName = "ויקטורי",
        iconURL ="https://www.victory.co.il/wp-content/uploads/2018/05/home-page.jpg",
        priceBaseURL = "http://matrixcatalog.co.il/NBCompetitionRegulations.aspx"
    ),
    MahsaniAshok(
        brandId = 3,
        brandName = "מחסני השוק",
        iconURL = "https://m-shuk.net/wp-content/uploads/2021/03/logo.png",
        priceBaseURL = VICTORY.priceBaseURL
    ),
    SuperBareket(
        brandId = 4,
        brandName = "סופר ברקת",
        iconURL = "https://scontent.ftlv5-1.fna.fbcdn.net/v/t1.18169-9/15492299_1035563226571581_6448037422821812281_n.png?_nc_cat=111&ccb=1-5&_nc_sid=09cbfe&_nc_ohc=Jfz4orEnBcsAX_ZdRgQ&_nc_ht=scontent.ftlv5-1.fna&oh=00_AT-R2T7qKY4KIgTJ03AKdcsZ4BMW6OhVpAEfMwIPkn8iaw&oe=621C609A",
        priceBaseURL = VICTORY.priceBaseURL
    ),
    HCohen(
        brandId = 5,
        brandName = "ח. כהן",
        iconURL = "http://hcohen.co.il/App_Themes/Cohen_Thems/Images/logo.jpg",
        priceBaseURL = VICTORY.priceBaseURL
    )


}
