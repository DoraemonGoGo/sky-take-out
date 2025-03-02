package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;
    /**
     * 查询当前用户的所有地址信息
     *
     * @param addressBook
     */
    public List<AddressBook> list(AddressBook addressBook) {
        List<AddressBook> list = addressBookMapper.list(addressBook);
        return list;
    }

    /**
     * 添加地址
     *
     * @param addressBook
     */
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 查询默认地址
     *
     * @return
     */
    public List<AddressBook> defaultAddress() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(1);
        List<AddressBook> list = addressBookMapper.list(addressBook);
        return list;
    }

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    public AddressBook getById(Long id) {
        AddressBook addressBook = addressBookMapper.getById(id);
        return addressBook;
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     */
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     */
    public void setDefault(AddressBook addressBook) {
        // 先将所有地址设置为非默认
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.updatIsDefaulteByuserId(addressBook);

        // 再将指定地址设置为默认
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id删除地址
     *
     * @param id
     */
    public void delete(Long id) {
        AddressBook addressBook = new AddressBook();
        addressBook.setId(id);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.delete(addressBook);
    }
}
