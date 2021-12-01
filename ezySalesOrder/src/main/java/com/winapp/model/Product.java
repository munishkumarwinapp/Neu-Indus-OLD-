package com.winapp.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    /* String soNo; */
    String code;
    String description;
    String slNo;
    String productCode;
    String productName;
    String cQty;
    String lQty;
    String qty;
    String price;
    String retailPrice;
    String uom;
    String pcsPerCarton;
    String categoryCode;
    String subCategoryCode;
    String WholesalePrice;
    String productImage;
    String total;
    String subTotal;
    String tax;
    String taxType;
    String taxPerc;
    String netTotal;
    String specification;
    String newCategoryCode = "";
    String newSubCategoryCode = "";
    String haveAttribute = "";
    int return_qty;
    private Double lprice = 0.00;
    private Double cprice = 0.00;
    private int quantity = 0;
    private Double ext = 0.00;
    private int pcs = 0;
    private int cqty = 0;
    private int lqty = 0;
    private Double taxPer = 0.00;
    private Double subTot = 0.00;
    private Double netTot = 0.00;
    private Double discount = 0.00;
    private Double minimumSellingPrice = 0.00;
    private Double minimumCartonSellingPrice = 0.00;
    private int focQty = 0;
    private int exchangeQty = 0;
    private String categoryName = "";
    private String subCategoryName = "";
    private Double stock = 0.00;
    private String catDisplayOrder = "";
    private String subCatDisplayOrder = "";
    private String prodDisplayOrder = "";
    private String haveBatch = "";
    private String haveExpiry = "";
    private String type;
    private String status;
    private String remarks = "";
    private boolean flag;
    private double balanceAmount = 0;
    private double paidAmount = 0;
    String no;
    String name;
    String colorCode;
    String colorName;
    String sizeCode;
    String sizeName;
    String tranDate;
    String salesQty;
    String salesSubTotal;
    String purchaseQty;
    String purchaseSubTotal;
    String cost;
    Bitmap bitmap;
    private static String path;
    private static String headerpath;
    private static String merchandiseflag;
    private static String foc;
    private static String exchange;
    private static String pcsPer;
    private double totCalculate;

    public static String getHeaderpath() {
        return headerpath;
    }

    public static void setHeaderpath(String headerpath) {
        Product.headerpath = headerpath;
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        Product.path = path;
    }

    public Product() {

    }

    public Product(Parcel in) {
        productCode = in.readString();
        productName = in.readString();
        cQty = in.readString();
        lQty = in.readString();
        qty = in.readString();
        retailPrice = in.readString();
        uom = in.readString();
        pcsPerCarton = in.readString();
        categoryCode = in.readString();
        subCategoryCode = in.readString();
        WholesalePrice = in.readString();
        productImage = in.readString();
        specification = in.readString();
        slNo = in.readString();
        colorCode = in.readString();
        colorName = in.readString();
        sizeCode = in.readString();
        sizeName = in.readString();
        haveAttribute = in.readString();
    }

    public Product(String name, String productImage) {
        this.productName = name;
        this.productImage = productImage;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductName() {
        return productName;
    }

    public String getHaveBatch() {
        return haveBatch;
    }

    public void setHaveBatch(String haveBatch) {
        this.haveBatch = haveBatch;
    }

    public String getHaveExpiry() {
        return haveExpiry;
    }

    public void setHaveExpiry(String haveExpiry) {
        this.haveExpiry = haveExpiry;
    }

    public void setProductName(String productname) {
        this.productName = productname;
    }

    public String getSlNo() {
        return slNo;
    }

    public void setSlNo(String slNo) {
        this.slNo = slNo;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productcode) {
        this.productCode = productcode;
    }

    public String getCqty() {
        return cQty;
    }

    public void setCqty(String cqty) {
        this.cQty = cqty;
    }

    public String getLqty() {
        return lQty;
    }

    public void setLqty(String lqty) {
        this.lQty = lqty;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getPcspercarton() {
        return pcsPerCarton;
    }

    public void setPcspercarton(String pcspercarton) {
        this.pcsPerCarton = pcspercarton;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getSubCategoryCode() {
        return subCategoryCode;
    }

    public void setSubCategoryCode(String subCategoryCode) {
        this.subCategoryCode = subCategoryCode;
    }

    public String getWholesalePrice() {
        return WholesalePrice;
    }

    public void setWholesalePrice(String wholesalePrice) {
        WholesalePrice = wholesalePrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getcQty() {
        return cQty;
    }

    public void setcQty(String cQty) {
        this.cQty = cQty;
    }

    public Double getMinimumCartonSellingPrice() {
        return minimumCartonSellingPrice;
    }

    public void setMinimumCartonSellingPrice(Double minimumCartonSellingPrice) {
        this.minimumCartonSellingPrice = minimumCartonSellingPrice;
    }

    public String getHaveAttribute() {
        return haveAttribute;
    }

    public void setHaveAttribute(String haveAttribute) {
        this.haveAttribute = haveAttribute;
    }

    /*
             * public String getlQty() { return lQty; } public void setlQty(String lQty)
             * { this.lQty = lQty; }
             */
    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getTaxPerc() {
        return taxPerc;
    }

    public void setTaxPerc(String taxPerc) {
        this.taxPerc = taxPerc;
    }

    public String getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(String netTotal) {
        this.netTotal = netTotal;
    }

    public Double getLprice() {
        return lprice;
    }

    public void setLprice(Double lprice) {
        this.lprice = lprice;
    }

    public Double getCprice() {
        return cprice;
    }

    public void setCprice(Double cprice) {
        this.cprice = cprice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getExt() {
        return ext;
    }

    public void setExt(Double ext) {
        this.ext = ext;
    }

    public int getPcs() {
        return pcs;
    }

    public void setPcs(int pcs) {
        this.pcs = pcs;
    }

    public Double getTaxPer() {
        return taxPer;
    }

    public void setTaxPer(Double taxPer) {
        this.taxPer = taxPer;
    }

    public Double getSubTot() {
        return subTot;
    }

    public void setSubTot(Double subTot) {
        this.subTot = subTot;
    }

    public Double getNetTot() {
        return netTot;
    }

    public void setNetTot(Double netTot) {
        this.netTot = netTot;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getMinimumSellingPrice() {
        return minimumSellingPrice;
    }

    public void setMinimumSellingPrice(Double minimumSellingPrice) {
        this.minimumSellingPrice = minimumSellingPrice;
    }

    public int getFocQty() {
        return focQty;
    }

    public void setFocQty(int focQty) {
        this.focQty = focQty;
    }

    public int getExchangeQty() {
        return exchangeQty;
    }

    public void setExchangeQty(int exchangeQty) {
        this.exchangeQty = exchangeQty;
    }

    public int getCQty() {
        return cqty;
    }

    public void setCQty(int cqty) {
        this.cqty = cqty;
    }

    public int getLQty() {
        return lqty;
    }

    public void setLQty(int lqty) {
        this.lqty = lqty;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public Double getStock() {
        return stock;
    }

    public void setStock(Double stock) {
        this.stock = stock;
    }

    public String getCatDisplayOrder() {
        return catDisplayOrder;
    }

    public void setCatDisplayOrder(String catDisplayOrder) {
        this.catDisplayOrder = catDisplayOrder;
    }

    public String getSubCatDisplayOrder() {
        return subCatDisplayOrder;
    }

    public void setSubCatDisplayOrder(String subCatDisplayOrder) {
        this.subCatDisplayOrder = subCatDisplayOrder;
    }

    public String getProdDisplayOrder() {
        return prodDisplayOrder;
    }

    public void setProdDisplayOrder(String prodDisplayOrder) {
        this.prodDisplayOrder = prodDisplayOrder;
    }

    public String getNewCategoryCode() {
        return newCategoryCode;
    }

    public void setNewCategoryCode(String newCategoryCode) {
        this.newCategoryCode = newCategoryCode;
    }

    public String getNewSubCategoryCode() {
        return newSubCategoryCode;
    }

    public void setNewSubCategoryCode(String newSubCategoryCode) {
        this.newSubCategoryCode = newSubCategoryCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getSizeCode() {
        return sizeCode;
    }

    public void setSizeCode(String sizeCode) {
        this.sizeCode = sizeCode;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getTranDate() {
        return tranDate;
    }

    public void setTranDate(String tranDate) {
        this.tranDate = tranDate;
    }

    public String getSalesQty() {
        return salesQty;
    }

    public void setSalesQty(String salesQty) {
        this.salesQty = salesQty;
    }

    public String getSalesSubTotal() {
        return salesSubTotal;
    }

    public void setSalesSubTotal(String salesSubTotal) {
        this.salesSubTotal = salesSubTotal;
    }

    public String getPurchaseQty() {
        return purchaseQty;
    }

    public void setPurchaseQty(String purchaseQty) {
        this.purchaseQty = purchaseQty;
    }

    public String getPurchaseSubTotal() {
        return purchaseSubTotal;
    }

    public void setPurchaseSubTotal(String purchaseSubTotal) {
        this.purchaseSubTotal = purchaseSubTotal;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(productCode);
        dest.writeString(productName);
        dest.writeString(cQty);
        dest.writeString(lQty);
        dest.writeString(qty);
        dest.writeString(retailPrice);
        dest.writeString(uom);
        dest.writeString(pcsPerCarton);
        dest.writeString(categoryCode);
        dest.writeString(subCategoryCode);
        dest.writeString(WholesalePrice);
        dest.writeString(productImage);
        dest.writeString(specification);
        dest.writeString(slNo);
        dest.writeString(colorCode);
        dest.writeString(colorName);
        dest.writeString(sizeCode);
        dest.writeString(sizeName);
        dest.writeString(haveAttribute);
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public static String getMerchandiseflag() {
        return merchandiseflag;
    }

    public static void setMerchandiseflag(String merchandiseflag) {
        Product.merchandiseflag = merchandiseflag;
    }

    public static String getFoc() {
        return foc;
    }

    public static void setFoc(String foc) {
        Product.foc = foc;
    }

    public static String getExchange() {
        return exchange;
    }

    public static void setExchange(String exchange) {
        Product.exchange = exchange;
    }

    public static String getPcsPer() {
        return pcsPer;
    }

    public static void setPcsPer(String pcsPer) {
        Product.pcsPer = pcsPer;
    }

    public int getReturn_qty() {
        return return_qty;
    }

    public void setReturn_qty(int return_qty) {
        this.return_qty = return_qty;
    }

    public double getTotCalculate() {
        return totCalculate;
    }

    public void setTotCalculate(double totCalculate) {
        this.totCalculate = totCalculate;
    }
}