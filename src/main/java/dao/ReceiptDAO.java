package dao;

import model.Receipt;
import model.ServiceOrder;

import java.util.List;

public interface ReceiptDAO {

    Receipt save(Receipt receipt);

    Receipt findById(String transactionId);

    List<Receipt> findByOrder(ServiceOrder order);
}
