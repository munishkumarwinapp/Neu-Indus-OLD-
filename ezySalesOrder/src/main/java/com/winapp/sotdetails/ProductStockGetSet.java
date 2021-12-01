package com.winapp.sotdetails;

import java.util.Comparator;

public class ProductStockGetSet {

	String Product_Name;
	String Product_Code;
	String Product_Quantity;
	String Product_PcsPerCarton;
	String looseqty;
	int cartonqty;
	String wholeSalePrice;
	String Location;
	String weight;
	String qty;
	 String issueqty;
	String Purchase_Quantity;
	String AveragePurchaseQty;
	String PurchaseAmount;
	String AveragePurchaseCost;
	String SalesQty;
	String AverageSalesQty;
	String SalesAmount;
	String ProfitAmount;
	String OutletPrice;
	String BalanceQty;
	String AverageCost;
	String UnitCost;
	String Margin_perc;
	String barcode ;
	String slno;
	public ProductStockGetSet() {

	}

	public ProductStockGetSet(String Product_Name, String Product_Code,
			String Product_Quantity, String Product_PcsPerCarton,
			int cartonqty, String looseqty) {
		this.Product_Name = Product_Name;
		this.Product_Code = Product_Code;
		this.Product_Quantity = Product_Quantity;
		this.Product_PcsPerCarton = Product_PcsPerCarton;
		this.looseqty = looseqty;
		this.cartonqty = cartonqty;
	}

	public String getProduct_Name() {
		return Product_Name;
	}

	public void setProduct_Name(String product_Name) {
		Product_Name = product_Name;
	}

	public String getProduct_Code() {
		return Product_Code;
	}

	public void setProduct_Code(String product_Code) {
		Product_Code = product_Code;
	}

	public String getProduct_Quantity() {
		return Product_Quantity;
	}

	public void setProduct_Quantity(String product_Quantity) {
		Product_Quantity = product_Quantity;
	}

	public String getProduct_PcsPerCarton() {
		return Product_PcsPerCarton;
	}

	public void setProduct_PcsPerCarton(String product_PcsPerCarton) {
		Product_PcsPerCarton = product_PcsPerCarton;
	}

	public String getLooseqty() {
		return looseqty;
	}

	public void setLooseqty(String looseqty) {
		this.looseqty = looseqty;
	}

	public int getCartonqty() {
		return cartonqty;
	}

	public void setCartonqty(int cartonqty) {
		this.cartonqty = cartonqty;
	}

	public String getWholeSalePrice() {
		return wholeSalePrice;
	}

	public void setWholeSalePrice(String wholeSalePrice) {
		this.wholeSalePrice = wholeSalePrice;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getIssueqty() {
		return issueqty;
	}

	public void setIssueqty(String issueqty) {
		this.issueqty = issueqty;
	}

	public String getPurchase_Quantity() {
		return Purchase_Quantity;
	}

	public void setPurchase_Quantity(String purchase_Quantity) {
		Purchase_Quantity = purchase_Quantity;
	}

	public String getAveragePurchaseQty() {
		return AveragePurchaseQty;
	}

	public void setAveragePurchaseQty(String averagePurchaseQty) {
		AveragePurchaseQty = averagePurchaseQty;
	}

	public String getPurchaseAmount() {
		return PurchaseAmount;
	}

	public void setPurchaseAmount(String purchaseAmount) {
		PurchaseAmount = purchaseAmount;
	}

	public String getAveragePurchaseCost() {
		return AveragePurchaseCost;
	}

	public void setAveragePurchaseCost(String averagePurchaseCost) {
		AveragePurchaseCost = averagePurchaseCost;
	}

	public String getSalesQty() {
		return SalesQty;
	}

	public void setSalesQty(String salesQty) {
		SalesQty = salesQty;
	}

	public String getAverageSalesQty() {
		return AverageSalesQty;
	}

	public void setAverageSalesQty(String averageSalesQty) {
		AverageSalesQty = averageSalesQty;
	}

	public String getSalesAmount() {
		return SalesAmount;
	}

	public void setSalesAmount(String salesAmount) {
		SalesAmount = salesAmount;
	}

	public String getProfitAmount() {
		return ProfitAmount;
	}

	public void setProfitAmount(String profitAmount) {
		ProfitAmount = profitAmount;
	}

	public String getOutletPrice() {
		return OutletPrice;
	}

	public void setOutletPrice(String outletPrice) {
		OutletPrice = outletPrice;
	}

	public String getBalanceQty() {
		return BalanceQty;
	}

	public void setBalanceQty(String balanceQty) {
		BalanceQty = balanceQty;
	}

	public String getAverageCost() {
		return AverageCost;
	}

	public void setAverageCost(String averageCost) {
		AverageCost = averageCost;
	}

	public String getUnitCost() {
		return UnitCost;
	}

	public void setUnitCost(String unitCost) {
		UnitCost = unitCost;
	}

	public String getMargin_perc() {
		return Margin_perc;
	}

	public void setMargin_perc(String margin_perc) {
		Margin_perc = margin_perc;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getSlno() {
		return slno;
	}

	public void setSlno(String slno) {
		this.slno = slno;
	}

	public static Comparator<ProductStockGetSet> productnameComparator = new Comparator<ProductStockGetSet>() {

		public int compare(ProductStockGetSet s1, ProductStockGetSet s2) {
			String productname1 = s1.getProduct_Name().toUpperCase();
			String productname2 = s2.getProduct_Name().toUpperCase();

			//ascending order
			return productname1.compareTo(productname2);

			//descending order
			//return StudentName2.compareTo(StudentName1);
		}};

	/*Comparator for sorting the list by roll no*/
	public static Comparator<ProductStockGetSet> SalesqtyComp = new Comparator<ProductStockGetSet>() {

		public int compare(ProductStockGetSet s1, ProductStockGetSet s2) {

			String sqty1 = s1.getSalesQty();
			String sqty2 = s2.getSalesQty();
			if(sqty1!=null && !sqty1.isEmpty()){
			}else{
				sqty1 = "0";
			}
			if(sqty2!=null && !sqty2.isEmpty()){
			}else{
				sqty2 = "0";
			}

			double salesqty1 = Double.parseDouble(sqty1);
			double salesqty2 = Double.parseDouble(sqty2);

	   /*For ascending order*/
			double diff = salesqty2 - salesqty1;
			int dif = (int) Double.parseDouble(String.valueOf(diff));

	   /*For ascending order*/
			return dif;

	   /*For descending order*/
			//rollno2-rollno1;
		}};

	public static Comparator<ProductStockGetSet> PurchaseqtyComp = new Comparator<ProductStockGetSet>() {

		public int compare(ProductStockGetSet s1, ProductStockGetSet s2) {

			String pqty1 = s1.getPurchase_Quantity();
			String pqty2 = s2.getPurchase_Quantity();
			if(pqty1!=null && !pqty1.isEmpty()){
			}else{
				pqty1 = "0";
			}
			if(pqty2!=null && !pqty2.isEmpty()){
			}else{
				pqty2 = "0";
			}

			double purchaseqty1 = Double.parseDouble(pqty1);
			double purchaseqty2 = Double.parseDouble(pqty2);

	   /*For ascending order*/
			double diff = purchaseqty1 - purchaseqty2;
			int dif = (int) Double.parseDouble(String.valueOf(diff));

	   /*For ascending order*/
			return dif;

	   /*For descending order*/
			//rollno2-rollno1;
		}};

	public static Comparator<ProductStockGetSet> BalanceqtyComp = new Comparator<ProductStockGetSet>() {

		public int compare(ProductStockGetSet s1, ProductStockGetSet s2) {

			int dif = 0;
			try {
				String bqty1 = s1.getBalanceQty();
				String bqty2 = s2.getBalanceQty();
				if (bqty1 != null && !bqty1.isEmpty()) {
				} else {
					bqty1 = "0";
				}
				if (bqty2 != null && !bqty2.isEmpty()) {
				} else {
					bqty2 = "0";
				}
				double Balanceqty1 = Double.parseDouble(bqty1);
				double Balanceqty2 = Double.parseDouble(bqty2);

	   /*For ascending order*/
				double diff = Balanceqty1 - Balanceqty2;
				dif = (int) Double.parseDouble(String.valueOf(diff));

	   /*For descending order*/
				//rollno2-rollno1;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			return dif;
		}};

	public static Comparator<ProductStockGetSet> ProfitamountComp = new Comparator<ProductStockGetSet>() {

		public int compare(ProductStockGetSet s1, ProductStockGetSet s2) {

			String s1ProfitAmount = s1.getProfitAmount();
			String s2ProfitAmount = s2.getProfitAmount();
			if(s1ProfitAmount!=null && !s1ProfitAmount.isEmpty()){
			}else{
				s1ProfitAmount = "0";
			}
			if(s2ProfitAmount!=null && !s2ProfitAmount.isEmpty()){
			}else{
				s2ProfitAmount = "0";
			}
			double ProfitAmount1 = Double.parseDouble(s1ProfitAmount);
			double ProfitAmount2 = Double.parseDouble(s2ProfitAmount);

	   /*For ascending order*/
			double diff = ProfitAmount1 - ProfitAmount2;
			int dif = (int) Double.parseDouble(String.valueOf(diff));

	   /*For ascending order*/
			return dif;

	   /*For descending order*/
			//rollno2-rollno1;
		}};

	public static Comparator<ProductStockGetSet> MarginpercComp = new Comparator<ProductStockGetSet>() {

		public int compare(ProductStockGetSet s1, ProductStockGetSet s2) {

			String marginperc1 = s1.getMargin_perc();
			String marginperc2 = s2.getMargin_perc();
			if(marginperc1!=null && !marginperc1.isEmpty()){
			}else{
				marginperc1 = "0";
			}
			if(marginperc2!=null && !marginperc2.isEmpty()){
			}else{
				marginperc2 = "0";
			}
			double marginper1 = Double.parseDouble(marginperc1);
			double marginper2 = Double.parseDouble(marginperc2);

	   /*For ascending order*/
			double diff = marginper1 - marginper2;
			int dif = (int) Double.parseDouble(String.valueOf(diff));

	   /*For ascending order*/
			return dif;

	   /*For descending order*/
			//rollno2-rollno1;
		}};
}
