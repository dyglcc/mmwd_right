package qfpay.wxshop.data.beans;import java.io.Serializable;import java.util.List;public  class OnekeybehalfItemBean implements Serializable{	private static final long serialVersionUID = 1L;    public void setSales(String sales) {        this.sales = sales;    }    public void setId(String id) {        this.id = id;    }    public void setOrder_type(String order_type) {        this.order_type = order_type;    }    public void setDescr(String descr) {        this.descr = descr;    }    public void setTitle(String title) {        this.title = title;    }    public void setIs_virtual(String is_virtual) {        this.is_virtual = is_virtual;    }    public void setItemimgs(List<ImageBean> itemimgs) {        this.itemimgs = itemimgs;    }    public void setSource(String source) {        this.source = source;    }    public void setCps_created(String cps_created) {        this.cps_created = cps_created;    }    public void setSort_order(String sort_order) {        this.sort_order = sort_order;    }    public void setProductid(String productid) {        this.productid = productid;    }    public void setStatus(String status) {        this.status = status;    }    public void setHas_hdimg(String has_hdimg) {        this.has_hdimg = has_hdimg;    }    public void setPrice(String price) {        this.price = price;    }    public void setPostage(String postage) {        this.postage = postage;    }    public void setImg(String img) {        this.img = img;    }    public void setCid(String cid) {        this.cid = cid;    }    public void setCreated(String created) {        this.created = created;    }    public void setModified(String modified) {        this.modified = modified;    }    public void setAmount(String amount) {        this.amount = amount;    }    public void setCps_modified(String cps_modified) {        this.cps_modified = cps_modified;    }    public void setUserid(String userid) {        this.userid = userid;    }    private String sales= "";	private String id = "";    public String getUserid() {        return userid;    }    public static long getSerialVersionUID() {        return serialVersionUID;    }    public String getSales() {        return sales;    }    public String getId() {        return id;    }    public String getOrder_type() {        return order_type;    }    public String getDescr() {        return descr;    }    public String getTitle() {        return title;    }    public String getIs_virtual() {        return is_virtual;    }    public List<ImageBean> getItemimgs() {        return itemimgs;    }    public String getSource() {        return source;    }    public String getCps_created() {        return cps_created;    }    public String getSort_order() {        return sort_order;    }    public String getProductid() {        return productid;    }    public String getStatus() {        return status;    }    public String getHas_hdimg() {        return has_hdimg;    }    public String getPrice() {        return price;    }    public String getPostage() {        return postage;    }    public String getImg() {        return img;    }    public String getCid() {        return cid;    }    public String getCreated() {        return created;    }    public String getModified() {        return modified;    }    public String getAmount() {        return amount;    }    public String getCps_modified() {        return cps_modified;    }    public String getCps_value() {        return cps_value;    }    public void setCps_value(String cps_value) {        this.cps_value = cps_value;    }    private String cps_value;    private String order_type = "";	private String descr = "";	private String title = "";	private String is_virtual = "";	private List<ImageBean> itemimgs;	private String source = "";	private String cps_created = "";	private String sort_order = "";	private String productid = "";	private String status = "";	private String has_hdimg = "";    private String price = "";    private String postage = "";    private String img = "";    private String cid = "";    private String created = "";    private String modified = "";    private String amount = "";    private String cps_modified = "";    private String userid = "";    private int is_agent_actived= 1;    public void setIs_agent_actived(int is_agent_actived) {        this.is_agent_actived = is_agent_actived;    }    public int getIs_agent_actived() {        return is_agent_actived;    }    public boolean isAni = false;    public boolean isMenuOpened = false;	    class ImageBean {        public String getUrl() {            return url;        }        public String getId() {            return id;        }        public String getIid() {            return iid;        }        private String url;        public void setUrl(String url) {            this.url = url;        }        public void setId(String id) {            this.id = id;        }        public void setIid(String iid) {            this.iid = iid;        }        private String id;        private String iid;    }}