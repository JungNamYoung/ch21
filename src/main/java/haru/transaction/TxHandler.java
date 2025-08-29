package haru.transaction;

public interface TxHandler {
  void begin();

  void commit();

  void rollback();

  void close();
}