package service;

public class QueryConstants {

    // User Queries
    public static final String UPDATE_PROFILE
            = "UPDATE users SET food_type_id=?, food_taste_id=?, food_preference_id=?, sweetTooth=? WHERE user_id=?";
    public static final String LOGIN
            = "SELECT role_id FROM users WHERE user_id = ? AND name = ?";
    public static final String GET_USER_PREFERENCES
            = "SELECT food_type_id, sweetTooth, food_preference_id, food_taste_id FROM users WHERE user_id = ?";
    public static final String CHECK_USER_FEEDBACK
            = "SELECT COUNT(*) FROM feedbacks WHERE user_id = ? AND menu_item_id = ? AND feedback_date = ?";
    public static final String INSERT_USER_VOTE
            = "INSERT INTO user_vote (user_id, vote_date) VALUES (?, ?)";
    public static final String CHECK_USER_VOTE
            = "SELECT COUNT(*) FROM user_vote WHERE user_id = ? AND vote_date = ?";

    // Menu Item Queries
    public static final String ADD_MENU_ITEM
            = "INSERT INTO menu_items (name, price, rating, meal_type_id, food_type_id, food_taste_id, food_preference_id, sweetTooth) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String UPDATE_MENU_ITEM
            = "UPDATE menu_items SET price = ?, availability_status = ? WHERE menu_item_id = ?";
    public static final String DELETE_MENU_ITEM
            = "DELETE FROM menu_items WHERE menu_item_id = ?";
    public static final String GET_NEXT_DAY_MENU_ITEMS
            = "SELECT mi.menu_item_id, rmi.rollout_item_id, mi.name, mi.price, mi.rating, mi.sentiments, rmi.rollout_date, "
            + "ft.food_type_name AS food_type_name, fp.food_preference_name AS food_preference_name, ft1.spice_level AS spice_level, mi.sweetTooth "
            + "FROM rollout_menu_items rmi "
            + "JOIN menu_items mi ON rmi.menu_item_id = mi.menu_item_id "
            + "JOIN food_type ft ON mi.food_type_id = ft.id "
            + "JOIN food_preference fp ON mi.food_preference_id = fp.id "
            + "JOIN food_taste ft1 ON mi.food_taste_id = ft1.id "
            + "WHERE rmi.meal_type_id = ? AND rmi.rollout_date = ? "
            + "ORDER BY "
            + "CASE WHEN mi.food_type_id = ? THEN 1 ELSE 0 END DESC, "
            + "CASE WHEN mi.food_preference_id = ? THEN 1 ELSE 0 END DESC, "
            + "CASE WHEN mi.food_taste_id = ? THEN 1 ELSE 0 END DESC, "
            + "CASE WHEN mi.sweetTooth = ? THEN 1 ELSE 0 END DESC";

    // Menu Queries
    public static final String SHOW_MENU
            = "SELECT DISTINCT menu_item_id, name, price, availability_status, menu_types.meal_type, sentiments "
            + "FROM menu_items "
            + "RIGHT JOIN menu_types ON menu_items.meal_type_id = menu_types.meal_type_id "
            + "ORDER BY menu_item_id";

    // Notification Queries
    public static final String ADD_NOTIFICATION
            = "INSERT INTO notifications (menu_item_id, message_id, notification_date) VALUES (?, ?, ?)";
    public static final String VIEW_NOTIFICATIONS
            = "SELECT n.menu_item_id, m.name, m.price, m.availability_status, nm.message "
            + "FROM notifications n "
            + "JOIN menu_items m ON n.menu_item_id = m.menu_item_id "
            + "JOIN notifiedMessages nm ON n.message_id = nm.Id "
            + "WHERE n.notification_date = ?";

    // Report Queries
    public static final String GENERATE_REPORT
            = "SELECT mi.name, mi.rating, mi.sentiments, mi.sentiment_score, COUNT(pm.menu_item_id) AS count "
            + "FROM prepared_menu pm "
            + "JOIN menu_items mi ON pm.menu_item_id = mi.menu_item_id "
            + "GROUP BY mi.name, mi.rating, mi.sentiments, mi.sentiment_score";

    // Recommendation Queries
    public static final String PROCESS_RECOMMENDATION
            = "SELECT mi.menu_item_id, mi.name AS menu_item_name, mi.price AS menu_item_price, "
            + "CONCAT_WS(' ', mi.sentiments, GROUP_CONCAT(f.comment SEPARATOR ' ')) AS sentiments_and_comments, "
            + "mi.rating AS menu_item_rating, (AVG(f.rating) + mi.rating) / 2 AS avg_combined_rating "
            + "FROM menu_items mi "
            + "LEFT JOIN feedbacks f ON mi.menu_item_id = f.menu_item_id "
            + "WHERE f.menu_item_id IS NOT NULL "
            + "GROUP BY mi.menu_item_id, mi.name, mi.price, mi.sentiments, mi.rating";
    public static final String STORE_RECOMMENDATION
            = "UPDATE menu_items SET sentiment_score = ?, sentiments = ?, rating = ? WHERE menu_item_id = ?";

    // Feedback Queries
    public static final String INSERT_FEEDBACK
            = "INSERT INTO feedbacks (menu_item_id, user_id, comment, rating, feedback_date) VALUES (?, ?, ?, ?, ?)";

    // Rollout Menu Item Queries
    public static final String STORE_SELECTED_ITEMS
            = "UPDATE rollout_menu_items SET vote = vote + 1 WHERE menu_item_id = ? AND rollout_date = ?";
    public static final String SELECT_ROLLOUT_MENU_ITEMS
            = "SELECT DISTINCT mi.menu_item_id, rmi.rollout_item_id, mi.name, mi.price, mi.rating, mi.sentiments, rmi.rollout_date, rmi.vote "
            + "FROM rollout_menu_items rmi "
            + "JOIN menu_items mi ON rmi.menu_item_id = mi.menu_item_id "
            + "WHERE rmi.meal_type_id = ? AND rmi.rollout_date = ?";
    public static final String INSERT_ROLLOUT_MENU_ITEM
            = "INSERT INTO rollout_menu_items (menu_item_id, rollout_date, meal_type_id) VALUES (?, ?, ?)";
    public static final String INSERT_PREPARED_MENU_ITEM
            = "INSERT INTO prepared_menu (menu_item_id, prepared_date) VALUES (?, CURRENT_DATE)";

    // Discard Item Queries
    public static final String SHOW_ELIGIBLE_DISCARD_ITEMS
            = "SELECT mi.menu_item_id, mi.name, mi.rating, mi.sentiments, mi.sentiment_score, mt.meal_type AS meal_type_name "
            + "FROM menu_items mi "
            + "JOIN menu_types mt ON mi.meal_type_id = mt.meal_type_id "
            + "WHERE mi.rating <= 2 AND mi.sentiment_score <= 50";
    public static final String STORE_DISCARD_MENU_ITEM
            = "INSERT INTO discard_items (menu_item_id, discard_date, message_id, discard_item_name) VALUES (?, CURRENT_DATE, ?, (SELECT name FROM menu_items WHERE menu_item_id = ?))";
    public static final String GET_DISCARD_MENU_ITEM_LIST
            = "SELECT discard_date, menu_item_id, message_id FROM discard_items ORDER BY discard_date DESC";
    public static final String GET_LATEST_DISCARDED_ITEM
            = "SELECT di.menu_item_id, discard_item_name, di.discard_date, di.message_id, nm.message "
            + "FROM discard_items di "
            + "LEFT JOIN menu_items mi ON di.menu_item_id = mi.menu_item_id "
            + "LEFT JOIN notifiedmessages nm ON di.message_id = nm.id "
            + "ORDER BY di.discard_date DESC "
            + "LIMIT 1";

    // Log Queries
    public static final String INSERT_LOG
            = "INSERT INTO user_login_logs(user_id, login_time, logout_time, operations) VALUES (?, ?, ?, ?)";
    public static final String SHOW_USER_LOGS
            = "SELECT l.log_id, l.user_id, u.name, r.role_name, l.login_time, l.logout_time, l.operations "
            + "FROM user_login_logs l "
            + "JOIN users u ON l.user_id = u.user_id "
            + "JOIN roles r ON u.role_id = r.role_id "
            + "ORDER BY l.log_id ASC";

    // Breakfast Queries
    public static final String GET_BREAKFAST_ITEMS
            = "SELECT mi.menu_item_id, mi.name, mt.meal_type, mi.price, mi.rating, mi.sentiments, mi.sentiment_score "
            + "FROM menu_items mi "
            + "JOIN menu_types mt ON mi.meal_type_id = mt.meal_type_id "
            + "WHERE mt.meal_type = 'Breakfast' "
            + "ORDER BY mi.rating DESC LIMIT 5";

    // Lunch Queries
    public static final String GET_LUNCH_ITEMS
            = "SELECT mi.menu_item_id, mi.name, mt.meal_type, mi.price, mi.rating, mi.sentiments, mi.sentiment_score "
            + "FROM menu_items mi "
            + "JOIN menu_types mt ON mi.meal_type_id = mt.meal_type_id "
            + "WHERE mt.meal_type = 'Lunch' "
            + "ORDER BY mi.rating DESC LIMIT 5";

    // Dinner Queries
    public static final String GET_DINNER_ITEMS
            = "SELECT mi.menu_item_id, mi.name, mt.meal_type, mi.price, mi.rating, mi.sentiments, mi.sentiment_score "
            + "FROM menu_items mi "
            + "JOIN menu_types mt ON mi.meal_type_id = mt.meal_type_id "
            + "WHERE mt.meal_type = 'Dinner' "
            + "ORDER BY mi.rating DESC LIMIT 5";
}
