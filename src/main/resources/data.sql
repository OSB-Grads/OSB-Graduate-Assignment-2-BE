INSERT INTO products (productId,productName,interestRate, fundingWindow,coolingPeriod,tenure,description)
 VALUES ("FD01","1 year plan" 6.1,3,1,10,"1 year plan with the interestRate of the 6.1");

INSERT INTO products (productId,productName,interestRate, fundingWindow,coolingPeriod,tenure,description)
 VALUES ("FD02","2 year plan" 6.4,3,2,20,"2 year plan with the interestRate of the 6.4");



 INSERT INTO Users (id,name,email,phone,created_At,updated_At,role)
  VALUES ("110011001100","WonderWoman ", "wonderwomen001@gmail.com", "1234567890",03-09-2025, 03-09-2025, "ADMIN");

 INSERT INTO Users (id,name,email,phone,created_At,updated_At,role)
    VALUES ("110011001100","LazyMan ", "lazyman420@gmail.com","0987654321",03-09-2025, 03-09-2025,"USER");


  @Column(name = "id" , updatable = false,nullable = false ,columnDefinition = "VARCHAR(36)")
      private UUID id;
      private String name;
      private String email;
      private String phone;
      private String created_At;
      private String updated_At;

      @Enumerated(EnumType.STRING)
      @Column(nullable = false)
      private Role role;

      public enum Role{
          ADMIN,
          USER,
      }
