package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;


    /**
     * 用户提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户提交订单:{}", ordersSubmitDTO);
        // 判断地址和订单是否为空
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list == null || list.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 生成订单
        Orders order = new Orders();
        String detailAddress = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        order.setUserId(BaseContext.getCurrentId());
        order.setAddress(detailAddress);
        order.setPhone(addressBook.getPhone());
        order.setUserId(addressBook.getUserId());
        order.setConsignee(addressBook.getConsignee());
        order.setUserName(addressBook.getConsignee());
        order.setOrderTime(LocalDateTime.now());
        order.setPayStatus(Orders.UN_PAID);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setNumber(String.valueOf(System.currentTimeMillis()));

        orderMapper.insert(order);

        // 向订单明细表中插入n条数据
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetails);

        // 清空购物车
        shoppingCartMapper.cleanByuserId(BaseContext.getCurrentId());

        // 封装返回数据
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        // 根据订单号查询当前用户的该订单
        Orders ordersDB = orderMapper.getByNumberAndUserId(ordersPaymentDTO.getOrderNumber(), userId);
        paySuccess(ordersPaymentDTO.getOrderNumber());
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
//        Orders orders = Orders.builder()
//                .id(ordersDB.getId())
//                .status(Orders.TO_BE_CONFIRMED)
//                .payStatus(Orders.PAID)
//                .checkoutTime(LocalDateTime.now())
//                .build();


        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders=new Orders();
        orders.setId(ordersDB.getId());
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setPayStatus(Orders.PAID);
        orders.setCheckoutTime(LocalDateTime.now());

        orderMapper.update(orders);
        //通过websocket向客户端浏览器推送消息type orderId content
//        Map map = new HashMap();
//        map.put("type",1);//1.来电提醒 2.客户催单
//        map.put("orderId", orders.getId());
//        map.put("content", "订单号："+outTradeNo);
//
//        String json = JSON.toJSONString(map);
//        webSocketServer.sendToAllClient(json);

    }

    /**
     * 查询订单列表
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    public PageResult pageQuery(int pageNum, int pageSize, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setStatus(status);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());

        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> list = new ArrayList<>();
        if (page != null && page.size() > 0) {
            for (Orders order : page) {
                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(order.getId());
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order, orderVO);
                orderVO.setOrderDetailList(orderDetails);
                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 查询订单详情
     *
     * @param orderId
     * @return
     */
    public OrderVO detail(Long orderId) {
        log.info("查询订单详情:{}", orderId);
        Orders order = orderMapper.getById(orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    /**
     * 取消订单
     *
     * @param orderId
     */
    public void userCancelById(Long orderId) throws Exception {
        log.info("取消订单:{}", orderId);
        Orders order = orderMapper.getById(orderId);
        // 校验订单是否存在
        if (order == null) {
            throw new AddressBookBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getStatus() > 2) {
            throw new AddressBookBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 订单处于待接单状态下取消订单需要退款
        if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            // 退款
//            weChatPayUtil.refund(
//                    order.getNumber(),
//                    order.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
            order.setPayStatus(Orders.REFUND);
        }
        order.setCancelReason("用户取消订单");
        order.setStatus(Orders.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    /**
     * 再来一单
     *
     * @param orderId
     */
    public void repetition(Long orderId) {
        // 将该订单再次放入购物车
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

        List<ShoppingCart> shoppingCartList = orderDetails.stream().map(orderDetail -> {
            ShoppingCart cart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, cart, "id");
            cart.setUserId(BaseContext.getCurrentId());
            cart.setCreateTime(LocalDateTime.now());
            return cart;
        }).collect(Collectors.toList());

        shoppingCartMapper.insertBatch(shoppingCartList);

    }

    /**
     * 条件查询订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        return null;
    }
}
