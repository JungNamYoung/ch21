package haru.transaction;

class TransactionalMetadata {
  private String transactionManagerName;
  private Class<?> serviceClass;

  TransactionalMetadata(String transactionManagerName, Class<?> serviceClass) {
    this.transactionManagerName = transactionManagerName;
    this.serviceClass = serviceClass;
  }

  public String getTransactionManagerName() {
    return transactionManagerName;
  }

  public Class<?> getServiceClass() {
    return serviceClass;
  }
}