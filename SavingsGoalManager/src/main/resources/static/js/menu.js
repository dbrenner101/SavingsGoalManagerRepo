require([
    "dijit/Menu",
    "dijit/MenuItem",
    "dijit/MenuBar",
    "dijit/MenuBarItem",
    "dijit/PopupMenuBarItem",
    "dijit/MenuSeparator",
    "dojo/domReady!"
], function(Menu, MenuItem, MenuBar, MenuBarItem, PopupMenuBarItem, MenuSeparator){
    // create the Menu container
    var mainMenu = new MenuBar({}, "mainMenu");

    // create Menu container and child MenuItems
    // for our sub-menu (no srcNodeRef; we will
    //add it under a PopupMenuItem)
    var dataAdminMenu = new Menu({
        id: "dataAdminMenu"
    });
    /*dataAdminMenu.addChild(new MenuItem({
        id: "addIncome",
        label: "Add an Income Item",
        onClick: function() {
        	location.assign("addNewIncomeStartWorkflow");
        }
    }));
    dataAdminMenu.addChild(new MenuItem({
    	id: "addExpense",
    	label: "Add an Expense Item",
    	onClick: function() {
    		location.assign("addNewExpenseStartWorkflow");
    	}
    }));
    dataAdminMenu.addChild(new MenuItem({
    	id: "addMonthlyBudget",
    	label: "Add a Monthly Budget",
    	onClick: function() {
    		location.assign("addMonthlyBudgetStartWorkflow");
    	}
    }));*/
    dataAdminMenu.addChild(new MenuItem({
        id: "addSavingsGoal",
        label: "Manage Savings Goals",
        onClick: function() {
        	location.assign("startAddSavingsGoalWorkflow");
        }
    }));
    dataAdminMenu.addChild(new MenuItem({
        id: "manageDeposits",
        label: "Manage Deposits",
        onClick: function() {
            location.assign("getUnallocatedDeposits");
        }
    }));
    dataAdminMenu.addChild(new MenuItem({
        id: "manageTransactions",
        label: "Manage Transaction",
        onClick: function() {
            location.assign("manageTransactions");
        }
    }));
    mainMenu.addChild(new PopupMenuBarItem({
        id: "dataAdmin",
        label: "Data Administration",
        popup: dataAdminMenu
    }));

    mainMenu.startup();
    dataAdminMenu.startup();
});
