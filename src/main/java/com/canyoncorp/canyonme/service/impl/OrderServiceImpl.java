package com.canyoncorp.canyonme.service.impl;

import com.canyoncorp.canyonme.domain.OrderLine;
import com.canyoncorp.canyonme.domain.Product;
import com.canyoncorp.canyonme.domain.PurchasedOrder;
import com.canyoncorp.canyonme.domain.enumeration.OrderState;
import com.canyoncorp.canyonme.domain.enumeration.PaymentMode;
import com.canyoncorp.canyonme.repository.PurchasedOrderRepository;
import com.canyoncorp.canyonme.service.OrderService;
import com.canyoncorp.canyonme.service.ProductService;
import com.canyoncorp.canyonme.service.UnavailableProductException;
import com.canyoncorp.canyonme.service.dto.OrderLineDTO;
import com.canyoncorp.canyonme.service.dto.ProductDTO;
import com.canyoncorp.canyonme.service.dto.PurchasedOrderDTO;
import com.canyoncorp.canyonme.service.mapper.ProductMapper;
import com.canyoncorp.canyonme.service.mapper.PurchasedOrderMapper;
import com.canyoncorp.canyonme.web.rest.vm.OrderLineVM;
import com.canyoncorp.canyonme.web.rest.vm.OrderVM;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = { UnavailableProductException.class })
public class OrderServiceImpl implements OrderService {

    private ProductService productService;
    private PurchasedOrderRepository purchasedOrderRepository;
    private PurchasedOrderMapper purchasedOrderMapper;
    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(
        ProductService productService,
        PurchasedOrderRepository purchasedOrderRepository,
        PurchasedOrderMapper purchasedOrderMapper
    ) {
        this.productService = productService;
        this.purchasedOrderRepository = purchasedOrderRepository;
        this.purchasedOrderMapper = purchasedOrderMapper;
    }

    @Transactional
    public List<ProductDTO> purchaseOrder(OrderVM order) {
        List<OrderLineDTO> orders = toOrderLineDTOS(order.getOrderLines());
        List<ProductDTO> remainingItems = new ArrayList<ProductDTO>();

        // applying purchase for each orderline
        try {
            for (OrderLineDTO orderLine : orders) {
                remainingItems.add(productService.purchase(orderLine).get());
            }
        } catch (UnavailableProductException e) {
            log.info("bad orderline found");
            throw e;
        } catch (Exception e) {
            log.error("unknown exception caught");
            System.out.println(e.getMessage());
            throw e;
        }

        // creating new purchasedOrder
        PurchasedOrderDTO purchasedOrderDTO = createPurchasedOrderDTO(order);
        System.out.println(purchasedOrderDTO);

        return remainingItems;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getBadOrderLinesProducts(List<OrderLineDTO> orders) {
        List<ProductDTO> products = new ArrayList<ProductDTO>();
        for (OrderLineDTO orderLine : orders) {
            Optional<ProductDTO> productDTO = productService.getProduct(orderLine);
            if (!productDTO.isPresent() || productDTO.get().getQuantity() < orderLine.getQuantity()) products.add(productDTO.get());
        }
        return products;
    }

    public List<OrderLineDTO> toOrderLineDTOS(List<OrderLineVM> orderLineVMS) {
        List<OrderLineDTO> orderLineDTOS = new ArrayList<OrderLineDTO>();

        // mapping OrderLineVM to OderLineDTO
        for (OrderLineVM orderLineVM : orderLineVMS) {
            OrderLineDTO orderLineDTO = new OrderLineDTO();
            orderLineDTO.setProduct(orderLineVM.getProductId());
            orderLineDTO.setQuantity(orderLineVM.getQuantity());
            orderLineDTOS.add(orderLineDTO);
        }
        return orderLineDTOS;
    }

    private PurchasedOrderDTO createPurchasedOrderDTO(OrderVM orderVM) {
        PurchasedOrderDTO purchasedOrderDTO = new PurchasedOrderDTO();
        purchasedOrderDTO.setOrderDate(LocalDate.now());
        purchasedOrderDTO.setOrderState(OrderState.NEW);
        purchasedOrderDTO.setBillingAddress(orderVM.getBillingAddress());
        purchasedOrderDTO.setShippingAddress(orderVM.getShippingAddress());
        purchasedOrderDTO.setPaymentMode(orderVM.getPaymentMode().toString());
        purchasedOrderDTO.setShippingMode(orderVM.getShippingMode().toString());
        // TODO: set Person

        // inserting to db
        PurchasedOrder purchasedOrder = purchasedOrderMapper.toEntity(purchasedOrderDTO);
        return purchasedOrderMapper.toDto(purchasedOrderRepository.save(purchasedOrder));
    }
}
