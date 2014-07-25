use Restaurant;

-- permissions

call createPermission('PERM_CREATE_ACCOUNTS');
call createPermission('PERM_READ_ACCOUNTS');
call createPermission('PERM_UPDATE_ACCOUNTS');
call createPermission('PERM_DELETE_ACCOUNTS');
call createPermission('PERM_ADMIN_ACCOUNTS');

call createPermission('PERM_CREATE_ORDERS');
call createPermission('PERM_READ_ORDERS');
call createPermission('PERM_UPDATE_ORDERS');
call createPermission('PERM_DELETE_ORDERS');
call createPermission('PERM_UPDETE_COOCED_ITEMS');
call createPermission('PERM_ADMIN_ORDERS');

call createPermission('PERM_CREATE_RESTAURANT_TABLES');
call createPermission('PERM_READ_RESTAURANT_TABLES');
call createPermission('PERM_UPDATE_RESTAURANT_TABLES');
call createPermission('PERM_DELETE_RESTAURANT_TABLES');
call createPermission('PERM_ADMIN_RESTAURANT_TABLES');

call createPermission('PERM_CREATE_MENU_ITEMS');
call createPermission('PERM_READ_MENU_ITEMS');
call createPermission('PERM_UPDATE_MENU_ITEMS');
call createPermission('PERM_DELETE_MENU_ITEMS');
call createPermission('PERM_ADMIN_MENU_ITEMS');

call createPermission('PERM_ADMIN_REPORTS');

-- roles

call createRole('ROLE_USER', @role_user);

call createRole('ROLE_COOK', @role_cook);
call roleHasPermission(@role_cook, 'PERM_READ_ORDERS');
call roleHasPermission(@role_cook, 'PERM_UPDETE_COOCED_ITEMS');

call createRole('ROLE_WAITER', @role_waiter);
call roleHasPermission(@role_waiter, 'PERM_READ_ORDERS');
call roleHasPermission(@role_waiter, 'PERM_CREATE_ORDERS');
call roleHasPermission(@role_waiter, 'PERM_UPDATE_ORDERS');

call createRole('ROLE_ADMIN', @role_admin);
call roleHasPermission(@role_admin, 'PERM_READ_ACCOUNTS');
call roleHasPermission(@role_admin, 'PERM_ADMIN_MENU_ITEMS');
call roleHasPermission(@role_admin, 'PERM_ADMIN_RESTAURANT_TABLES');
call roleHasPermission(@role_admin, 'PERM_ADMIN_ORDERS');
call roleHasPermission(@role_admin, 'PERM_ADMIN_REPORTS');

call createRole('ROLE_SECURITY_OFFICER', @security_officer);
call roleHasPermission(@security_officer, 'PERM_ADMIN_ACCOUNTS');

-- accounts

call createAccount('Peter', 'Kutz', 'peter', 'f', @peter);
call accountHasRole(@peter, @role_user);

call createAccount('Tomas', 'Marconi', 'tomas', 'f', @tomas);
call accountHasRole(@tomas, @role_cook);

call createAccount('Maks', 'Karpov', 'maks', 'f', @maks);
call accountHasRole(@maks, @role_waiter);

call createAccount('Kristina', 'Karpova', 'kristina', 'f', @kristina);
call accountHasRole(@kristina, @role_waiter);

call createAccount('Tanya', 'Bertsova', 'tanya', 'f', @tanya);
call accountHasRole(@tanya, @role_waiter);
call accountHasRole(@tanya, @role_admin);

call createAccount('Ivan', 'Kon', 'ivan', 'f', @ivan);
call accountHasRole(@ivan, @role_admin);

call createAccount('Pavel', 'Klin', 'pavel', 'f', @pavel);
call accountHasRole(@pavel, @security_officer);

-- menuCategories & menu items

call createMenuCategory('Barbecue', 11, '2014-05-24 12:25:11', 1, @barbecue);
call createMenuItem('Lamb shish kebab', 'Finest lamb marinated and grilled', '250 g', 19900,
                    @barbecue, 'KITCHEN', 'default.jpg', 1, '2014-05-24 12:25:12', 0, @b1);
call createMenuItem('Chicken shish kebab', 'Grilled marinated chicken fllet', '250 g', 11500,
                    @barbecue, 'KITCHEN', 'chicken-kebab.jpg', 2, '2014-05-24 12:25:13', 0, @b2);
call createMenuItem('Veal kebab','Specially marinated and grilled veal','250 g',18900,@barbecue,
                    'KITCHEN','veal-kebab.jpg',2, '2014-05-24 12:25:14', 1, @b3);
call createMenuItem('Chicken kebab','Grilled marinated chicken fllet','250 g',11500,@barbecue,
                    'KITCHEN','chicken-kebab.jpg',3, '2014-05-24 12:25:15', 1,@b4);
call createMenuItem('Lamb kebab','Finest lamb marinated and grilled','250 g',19900,@barbecue,
                    'KITCHEN','lamb-kebab.jpg',1, '2014-05-24 12:25:16', 1,@b5);
call createMenuItem('Pork kebab','Specially marinated and grilled pork','250 g',17000,@barbecue,
                    'KITCHEN','pork-kebab.jpg',4, '2014-05-24 12:25:17', 1,@b6);
call createMenuItem('Lamb and veal shish kebab','','250 g',16900,@barbecue,'KITCHEN',
                    'lamb-and-veal-shish-kebab.jpg',5, '2014-05-24 12:25:18', 1,@b7);
call createMenuItem('Grilled vegetables','Grilled zucchini, peppers, squash and onion in a garlic
                     herb seasoning','300 g',9200,@barbecue,'KITCHEN','grilled-vegetables.jpg',6,
                    '2014-05-24 12:25:19', 1,@b8);

call createMenuCategory('Salad', 2, '2014-05-24 12:28:10', 1, @salads);
call createMenuItem('Greek salad','Tomatoes, green pepper, olives,
red onion and cucumbers dressed with olive oil and finished with crumbled feta cheese','200 g',
                    7000,@salads,'KITCHEN','greek-salad.jpg',5, '2014-05-24 12:28:11', 1,@s1);
call createMenuItem('Vegetable salad','Fresh vegetables, walnuts,
sheep cheese dressed with walnut oil','150 g',5550,@salads,'KITCHEN','vegetable-salad.jpg',6,
                    '2014-05-24 12:28:12' ,0,@s2);
call createMenuItem('Caesar salad with shrimps','Romaine lettuce and croutons dressed with
parmesan cheese, lemon juice, olive oil and topped with grilled shrimps','250 g',9400,@salads,
                    'KITCHEN','caesar-salad-with-shrimp.jpg',7, '2014-05-24 12:28:13', 1,@s3);
call createMenuItem('Caesar salad with chicken','Romaine lettuce and croutons dressed with
parmesan cheese, lemon juice, olive oil and topped with grilled chicken','250 g',8800,@salads,
                    'KITCHEN','caesar-salad-with-chicken.jpg',8, '2014-05-24 12:28:14', 1,@s4);
call createMenuItem('Caprese salad','Sliced fresh mozzarella, tomatoes and basil seasoned with
salt and olive oil','200 g',6800,@salads,'KITCHEN','caprese-salad.jpg',10, '2014-05-24 12:28:15',
                    0,@s5);
call createMenuItem('Duck breast salad','Duck breast halves, radicchio, frisee, walnuts,
orange peel dressed with sherry vinegar and olive oil','200 g',11000,@salads,'KITCHEN',
                    'duck-breast-salad.jpg',11, '2014-05-24 12:28:16', 1,@s6);
call createMenuItem('Vegetable salad','Fresh vegetables, walnuts,
sheep cheese dressed with walnut oil','150 g',5100,@salads,'KITCHEN','vegetable-salad.jpg',6,
                    '2014-05-24 12:28:17', 1,@s7);
call createMenuItem('Caprese salad','Sliced fresh mozzarella, tomatoes and basil seasoned with
salt and olive oil','200 g',6800,@salads,'KITCHEN','caprese-salad.jpg',10, '2014-05-24 12:28:18',
                    1,@s8);

call createMenuCategory('Grill', 10, '2014-05-24 12:58:10', 0, @grill);
call createMenuItem('Chicken shish kebab','Grilled marinated chicken fllet','250 g',11500,@grill,
                    'KITCHEN','default.jpg',2, '2014-05-24 12:58:11', 1,@g1);

call createMenuCategory('Cold appetizer', 4, '2014-05-24 12:48:10', 1, @cold_appetizer);
call createMenuItem('Marinated salmon carpaccio','Marinated salmon carpaccio with citrus and
parmesan flakes','140 g',13600,@cold_appetizer,'KITCHEN','marinated-salmon-carpaccio.jpg',21,
                    '2014-05-24 12:48:11', 1,@c1);
call createMenuItem('Beef carpaccio','Beef sirloin carpaccio with olive oil and parmesan flakes',
                    '140 g',11700,@cold_appetizer,'KITCHEN','beef-carpaccio.jpg',22,
                    '2014-05-24 12:48:12', 1,@c2);
call createMenuItem('Norwegian salmon tartar','Norwegian salmon tartar on toast with quail egg',
                    '170 g',14000,@cold_appetizer,'KITCHEN','norwegian-salmon-tartar.jpg',23,
                    '2014-05-24 12:48:13', 1,@c3);
call createMenuItem('Cheese plate','','200 g',17000,@cold_appetizer,'KITCHEN','cheese-plate.jpg',
                    19, '2014-05-24 12:48:14', 1,@c4);
call createMenuItem('Meet plate','','170 g',13500,@cold_appetizer,'KITCHEN','meet-plate.jpg',20,
                    '2014-05-24 12:48:15', 1,@c5);


call createMenuCategory('Salads', 3, '2014-05-24 12:48:19', 0, @s);

call createMenuCategory('First course', 6, '2014-05-24 12:13:10', 1, @first_course);
call createMenuItem('Ukrainian borsch','Traditional Ukrainian beetroot soup with French beans and
 pork ribs. Served with sour cream','250 g',5200,@first_course,'KITCHEN','ukrainian-borsch.jpg',
                    31, '2014-05-24 12:13:11', 1,@f1);
call createMenuItem('Pea soup','Pea soup with pork ribs','250 g',5200,@first_course,'KITCHEN',
                    'pea-soup.jpg',32, '2014-05-24 12:13:12', 1,@f2);
call createMenuItem('Onion soup','French Onion Soup made of onions, broth, salt, pepper','250 g',
                    7000,@first_course,'KITCHEN','onion-soup.jpg',33, '2014-05-24 12:13:13', 1,
                    @f3);

call createMenuCategory('Main course', 8, '2014-05-24 12:17:10', 1, @main_course);
call createMenuItem('Roast duck breast','Roast duck breast with asparagus, mashed potato,
caramelised shallot and hispi cabbage','250 g',18000,@main_course,'KITCHEN',
                    'roast-duck-breast.jpg',45, '2014-05-24 12:17:11', 1,@m1);
call createMenuItem('Baked Lemon Chicken','Baked lemon chicken with Chinese lemon sauce','250 g',
                    16500,@main_course,'KITCHEN','baked-lemon-chicken.jpg',46,
                    '2014-05-24 12:17:12', 1,@m2);
call createMenuItem('Filet Mignon','Pan seared filet of beef tenderloin with a red wine sauce',
                    '300 g',21000,@main_course,'KITCHEN','filet-mignon.jpg',47,
                    '2014-05-24 12:17:13', 1,@m3);
call createMenuItem('Beef stroganoff','Tender strips of beef and mushrooms cooked in sour cream',
                    '250 g',18000,@main_course,'KITCHEN','beef-stroganoff.jpg',48,
                    '2014-05-24 12:17:14', 1,@m4);
call createMenuItem('Escalopes of salmon','Broiled salmon filet with scallops and a soy sauce',
                    '300 g',24500,@main_course,'KITCHEN','escalopes-of-salmon.jpg',49,
                    '2014-05-24 12:17:15', 1,@m5);
call createMenuItem('Black cod filet','Broiled black cod filet with asparagus and a crab sauce',
                    '300 g',22000,@main_course,'KITCHEN','black-cod-filet.jpg',50,
                    '2014-05-24 12:17:16', 1,@m6);

call createMenuCategory('Coffee & Tea', 20, '2014-05-24 12:37:10', 1, @coffees_teas);
call createMenuItem('Espresso','Classical espresso brewed from best coffee beans with crema on
top','65 ml',2800,@coffees_teas,'BAR','espresso.jpg',61, '2014-05-24 12:37:11', 1,@cof1);
call createMenuItem('Americano','Classical espresso brewed from best coffee beans with hot
water','135 ml',3100,@coffees_teas,'BAR','americano.jpg',62, '2014-05-24 12:37:12', 1,@cof2);
call createMenuItem('Cappuccino','Espresso, hot milk and steam-milk foam','135 ml',3100,
                    @coffees_teas,'BAR','cappuccino.jpg',63, '2014-05-24 12:37:13', 1,@cof3);
call createMenuItem('Herb tea','','300 ml',4500,@coffees_teas,'BAR','herb-tea.jpg',64,
                    '2014-05-24 12:37:14', 1,@cof4);

call createMenuCategory('Wine', 22, '2014-05-24 12:41:10', 1, @wine);
call createMenuItem('Poggio il Castellare Brunello di Montalcino',
                    'Really powerful for the vintage, with plenty of ripe fruit and cedary new wood,
                     yet balanced and pretty. 2005 Italy','1 b',63000,@wine,'BAR',
                    'Poggio-il-Castellare-Brunello-di-Montalcino.jpg',71, '2014-05-24 12:41:11',
                    1,@w1);
call createMenuItem('Valfieri Barbera d''Asti','Aged in stainless steel, this is a fresh,
                     violet-scented and fruity expression of the classic Barbera of Asti.
                     2011 Italy','1 b',20000,@wine,'BAR','Valfieri-Barbera-dAsti.jpg',70,
                    '2014-05-24 12:41:12', 1,@w2);
call createMenuItem('Caprili Brunello di Montalcino','Good medium-dark red. Deep,
                     spicy scents of dark cherry, tobacco, minerals and herbs. 2008 Italy','1 b',
                    70000,@wine,'BAR','Caprili-Brunello-di-Montalcino.jpg',73,
                    '2014-05-24 12:41:13', 1,@w3);

-- tables

call createRestaurantTable('#1', '2014-05-24 12:34:56', 1);
call createRestaurantTable('#2', '2014-05-24 12:34:57', 1);
call createRestaurantTable('#3', '2014-05-24 12:34:58', 1);
call createRestaurantTable('#4', '2014-05-24 12:34:59', 1);
call createRestaurantTable('#5', '2014-05-24 12:35:10', 1);
call createRestaurantTable('#6', '2014-05-24 12:35:12', 1);
call createRestaurantTable('#7', '2014-05-24 12:35:13', 1);
call createRestaurantTable('#8', '2014-05-24 12:35:14', 1);
call createRestaurantTable('#5', '2014-05-24 12:35:15', 0);

-- orders

call createOrder(null, @maks, 'Oleg', 6, '2014-05-24 12:34:56', null, 'SUBMITTED',
                 '2014-05-24 12:34:56', 1, @o1);
call orderHasMenuItem(@s6, @o1, 1, 11000, 0, '2014-05-24 12:34:57', 1);
call orderHasMenuItem(@b3, @o1, 1, 16900, 0, '2014-05-24 12:34:59', 1);

call createOrder(null, @maks, 'Pavel', 6, '2014-05-24 11:24:56', null, 'SUBMITTED',
                 '2014-05-24 11:24:56', 1, @o2);
call orderHasMenuItem(@s6, @o2, 2, 11000, 0, '2014-05-24 11:24:58', 1);

call createOrder(@peter, @maks, null, 6, '2014-05-24 16:27:56', null, 'SUBMITTED',
                 '2014-05-24 16:27:56', 1, @o3);
call orderHasMenuItem(@b5, @o3, 1, 11500, 0, '2014-05-24 16:28:56', 1);

call createOrder(null, @tanya, 'Katya', 6, '2014-05-24 17:15:56', null, 'SUBMITTED',
                 '2014-05-24 17:15:56', 1, @o4);
call orderHasMenuItem(@s1, @o4, 1, 7000, 0, '2014-05-24 17:16:56', 1);
call orderHasMenuItem(@b3, @o4, 1, 16900, 0, '2014-05-24 17:17:56', 1);

call createOrder(null, @tanya, 'Katya', 6, '2014-05-24 17:14:56', null, 'SUBMITTED',
                 '2014-05-24 17:14:56', 0, @o5);
call orderHasMenuItem(@s1, @o5, 1, 7000, 0, '2014-05-24 17:14:43', 1);
call orderHasMenuItem(@b3, @o5, 1, 16900, 0, '2014-05-24 17:14:52', 1);

call createOrder(null, @tanya, 'Valya', 5, '2014-05-24 17:19:56', null, 'SUBMITTED',
                 '2014-05-24 17:19:56', 1, @o6);

call createOrder(null, null, 'Pasha', 5, '2014-05-24 13:19:56', null, 'SUBMITTED',
                 '2014-05-24 13:19:56', 1, @o7);

call createOrder(null, @tanya, 'Cris', 5, '2014-05-24 12:19:56', null, 'UNPAID',
                 '2014-05-24 12:19:56', 1, @o8);

call createOrder(null, @maks, 'Pavel23', 6, '2014-05-24 10:44:56', null, 'SUBMITTED',
                 '2014-05-24 10:44:56', 1, @o9);
call orderHasMenuItem(@c2, @o9, 1, 11700, 1, '2014-05-24 10:44:57', 1);
call orderHasMenuItem(@f3, @o9, 1, 7000, 0, '2014-05-24 10:45:32', 1);
call orderHasMenuItem(@f2, @o9, 1, 5200, 0, '2014-05-24 10:45:40', 0);
call orderHasMenuItem(@m5, @o9, 1, 24500, 0, '2014-05-24 10:45:50', 1);
call orderHasMenuItem(@w1, @o9, 1, 63000, 0, '2014-05-24 10:46:56', 1);

call createOrder(null, @maks, 'Pavel24', 6, '2014-05-24 18:23:56', null, 'SUBMITTED',
                 '2014-05-24 18:23:56', 1, @o10);
call orderHasMenuItem(@s6, @o10, 1, 11000, 0, '2014-05-24 18:23:59', 1);
call orderHasMenuItem(@f3, @o10, 1, 7000, 0, '2014-05-24 18:23:12', 0);
call orderHasMenuItem(@f2, @o10, 1, 5200, 0, '2014-05-24 18:23:33', 1);
