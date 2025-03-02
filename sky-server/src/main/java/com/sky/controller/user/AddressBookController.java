package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "C端-地址簿接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询当前用户的所有地址簿
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询地址簿")
    public Result<List<AddressBook>> list() {
        log.info("查询地址簿");
        AddressBook addressBook = new AddressBook();
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result add(@RequestBody AddressBook addressBook) {
        log.info("新增地址：{}", addressBook);
        addressBookService.add(addressBook);
        return Result.success();
    }

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        log.info("根据id查询地址：{}", id);
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 查询默认地址
     *
     * @param
     * @return
     */
    @GetMapping("default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> defaultAddress() {
        log.info("查询默认地址");
        List<AddressBook> list = addressBookService.defaultAddress();
        if (list != null && list.size() == 1) {
            return Result.success(list.get(0));
        }
        return Result.error("未设置默认地址");
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("修改地址")
    public Result update(@RequestBody AddressBook addressBook) {
        log.info("根据id修改地址：{}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        log.info("设置默认地址：{}", addressBook);
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     *
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result delete(Long id) {
        log.info("根据id删除地址：{}", id);
        addressBookService.delete(id);
        return Result.success();
    }
}
