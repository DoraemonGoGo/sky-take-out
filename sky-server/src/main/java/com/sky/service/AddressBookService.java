package com.sky.service;


import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 查询当前用户的所有地址信息
     *
     * @param addressBook
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 添加地址
     *
     * @param addressBook
     */
    void add(AddressBook addressBook);

    /**
     * 查询默认地址
     *
     * @return
     */
    List<AddressBook> defaultAddress();

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 修改地址
     *
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 设置默认地址
     *
     * @param addressBook
     */
    void setDefault(AddressBook addressBook);

    /**
     * 根据id删除地址
     *
     * @param id
     */
    void delete(Long id);
}
