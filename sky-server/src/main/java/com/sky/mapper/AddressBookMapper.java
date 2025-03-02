package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressBookMapper {

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
    void insert(AddressBook addressBook);

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    /**
     * 修改地址
     *
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 将所有地址设为非默认
     *
     * @param addressBook
     */
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void updatIsDefaulteByuserId(AddressBook addressBook);

    /**
     * 删除地址
     *
     * @param addressBook
     */
    void delete(AddressBook addressBook);
}
