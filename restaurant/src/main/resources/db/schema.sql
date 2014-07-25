drop database if exists Restaurant;
create database Restaurant;
use Restaurant;

CREATE TABLE IF NOT EXISTS `Restaurant`.`Permissions`
(
  `PermissionId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `Name` VARCHAR(50) NOT NULL ,
  `Active` TINYINT(1) default 1 ,
  unique index NameIdx (Name)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Restaurant`.`Roles`
(
  `RoleId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `Name` VARCHAR(50) NOT NULL ,
  `Active` TINYINT(1) default 1 ,
  unique index NameIdx (Name)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Restaurant`.`Role_Permissions`
(
  `RolePermissionId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `RoleId` INT NOT NULL ,
  `PermissionId` INT NOT NULL ,
  `Active` TINYINT(1) default 1 ,
  FOREIGN KEY (RoleId) REFERENCES Roles(RoleId) ,
  FOREIGN KEY (PermissionId) REFERENCES Permissions(PermissionId) ,
  unique index RolePermissionIdx (PermissionId, RoleId)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Restaurant`.`Accounts`
(
  `AccountId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `FirstName` VARCHAR(100) ,
  `LastName` VARCHAR(100) ,
  `Username` VARCHAR(100) not null ,
  `Password` varchar(64) ,
  `Active` TINYINT(1) default 1 ,
  unique index UsernameIdx (Username)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Restaurant`.`Account_Roles`
(
  `AccountRoleId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `RoleId` INT NOT NULL ,
  `AccountId` INT NOT NULL ,
  `Active` TINYINT(1) default 1 ,
  FOREIGN KEY (RoleId) REFERENCES Roles(RoleId) ,
  FOREIGN KEY (AccountId) REFERENCES Accounts(AccountId) ,
  unique index AccountRoleIdx (RoleId, AccountId)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Restaurant`.`MenuCategories`
(
  `MenuCategoryId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `Title` VARCHAR(50) NOT NULL ,
  `Priority` INT ,
  `CreateTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  `Active` TINYINT(1) default 1
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Restaurant`.`MenuItems`
(
  `MenuItemId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `Title` VARCHAR(255) NOT NULL ,
  `Description` TEXT ,
  `Measure` VARCHAR(50) ,
  `Price` INT NOT NULL ,
  `MenuCategoryId` INT ,
  `CookingPlace` VARCHAR(20),
  `Picture` VARCHAR(225) default 'default.jpg' ,
  `Priority` INT ,
  `CreateTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  `Active` TINYINT(1) default 1 ,
  FOREIGN KEY (MenuCategoryId) REFERENCES MenuCategories(MenuCategoryId)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Restaurant`.`RestaurantTables`
(
  `RestaurantTableId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `TableNumber` varchar(30) not null ,
  `TableSize` smallint default 4 ,
  `CreateTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  `Active` TINYINT(1) default 1
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Restaurant`.`Orders`
(
  `OrderId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `GuestId` INT,
  `GuestName` varchar(30),
  `WaiterId` INT ,
  `RestaurantTableId` INT,
  `OrderTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  `Comments` TEXT ,
  `Status` VARCHAR(20) ,
  `CreateTime` TIMESTAMP ,
  `Active` TINYINT(1) default 1 ,
  FOREIGN KEY (GuestId) REFERENCES Accounts(AccountId) ,
  FOREIGN KEY (WaiterId) REFERENCES Accounts(AccountId) ,
  FOREIGN KEY (RestaurantTableId) REFERENCES RestaurantTables(RestaurantTableId)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `Restaurant`.`OrderDetails`
(
  `OrderDetailId` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `MenuItemId` INT ,
  `OrderId` INT ,
  `ItemQuantity` INT ,
  `ItemPrice` INT ,
  `Cooked` TINYINT(1) default 0 ,
  `CreateTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  `Active` TINYINT(1) default 1 ,
  FOREIGN KEY (MenuItemId) REFERENCES MenuItems(MenuItemId) ,
  FOREIGN KEY (OrderId) REFERENCES Orders(OrderId)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

delimiter //

create procedure createPermission($name varchar(50))
  begin
    insert into Permissions (Name) values ($name);
  end //

create procedure createRole($name varchar(50), out $roleId int)
  begin
    insert into Roles (Name) values ($name);
    set $roleId := last_insert_Id();
  end //

create procedure roleHasPermission($roleId int, $permName varchar(50))
  begin
    declare _permId int;
    select PermissionId from Permissions where Name = $permName into _permId;
    insert into role_permissions (roleId, permissionId) values ($roleId, _permId);
  end //

create procedure createAccount($firstName varchar(100), $lastName varchar(100),
  $username varchar(100), $password varchar(64), out $accountId int)
  begin
    insert into Accounts (FirstName, LastName, Username, Password)
      values ($firstName, $lastName, $username, $password);
    set $accountId := last_insert_Id();
  end //

create procedure accountHasRole($accountId int, $roleId int)
  begin
    insert into Account_Roles (AccountId, RoleId) values ($accountId, $roleId);
  end //

create procedure createMenuCategory($title varchar(50), $priority int, $createTime timestamp,
                                    $active TINYINT(1), out $menuCategoryId int)
  begin
    insert into MenuCategories (Title, Priority, CreateTime, Active)
      values ($title, $priority, $createTime, $active);
    set $menuCategoryId := last_insert_Id();
  end //

create procedure createMenuItem($title varchar(255), $description text, $measure varchar(50),
                                $price int, $menuCategoryId int, $cookingPlace VARCHAR(20),
                                $picture VARCHAR(225), $priority int, $createTime timestamp,
                                $active TINYINT(1), out $menuItemId int)
  begin
    insert into MenuItems (Title, Description, Measure, Price, MenuCategoryId, CookingPlace,
                           Picture, Priority, CreateTime, Active)
      values ($title, $description, $measure, $price, $menuCategoryId, $cookingPlace, $picture,
              $priority, $createTime, $active);
    set $menuItemId := last_insert_Id();
  end //

create procedure createRestaurantTable($tableNumber varchar(30), $createTime timestamp,
                                       $active TINYINT(1))
  begin
    insert into RestaurantTables (TableNumber, CreateTime, Active)
      values ($tableNumber, $createTime, $active);
  end //

create procedure createOrder($guestId int, $waiterId int, $guestName varchar(30),
                             $restaurantTableId int, $orderTime timestamp, $comments text,
                             $status varchar(20), $createTime timestamp, $active TINYINT(1),
                              out $orderId int)
  begin
    insert into Orders (GuestId, WaiterId, GuestName, RestaurantTableId, OrderTime, Comments,
                        Status, CreateTime, Active)
      values ($guestId, $waiterId, $guestName, $restaurantTableId, $orderTime, $comments, $status,
              $createTime, $active);
    set $orderId := last_insert_Id();
  end //

create procedure orderHasMenuItem($menuItemId int, $orderId int, $itemQuantity int, $itemPrice int,
                                  $cooked TINYINT(1), $createTime timestamp,
                                  $active TINYINT(1))
  begin
    insert into OrderDetails (MenuItemId, OrderId, ItemQuantity, ItemPrice, Cooked,
                              CreateTime, Active)
      values ($menuItemId, $orderId, $itemQuantity, $itemPrice,  $cooked, $createTime,
              $active);
  end //

delimiter ;

