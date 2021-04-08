package com.browser.mini;

public class BookMarkDB {
    private int _id;
    private String _name;
    private String _link;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_link() {
        return _link;
    }

    public void set_link(String _link) {
        this._link = _link;
    }

    public BookMarkDB(int _id, String _name, String _link) {
        this._id = _id;
        this._name = _name;
        this._link = _link;
    }

    public BookMarkDB() {
    }

    @Override
    public String toString() {
        return "BookMarkDB{" +
                "_id=" + _id +
                ", _name='" + _name + '\'' +
                ", _link='" + _link + '\'' +
                '}';
    }
}
