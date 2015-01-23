package qfpay.wxshop.data.model;

/**
 * 图片
 *
 * Created by LiFZhe on 1/19/15.
 */
public class PictureModel {
    private int    id; // 本地ID, 不需要存储Server ID, 但是存储到本地库的时候一定要先查询是否URL在本地存在再进行存储
    private String path;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PictureModel)) return false;

        PictureModel that = (PictureModel) o;

        if (id != that.id) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PictureModel{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
