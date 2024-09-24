package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * Controller class for the URL mappings for CoffeeMaker. The controller returns
 * the approprate HTML page in the /src/main/resources/templates folder. For a
 * larger application, this should be split across multiple controllers.
 *
 * @author Kai Presler-Marshall
 */
@Controller
public class MappingController {

    /**
     * On a GET request to /index, the IndexController will return
     * /src/main/resources/templates/index.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/index", "/" } )
    public String index ( final Model model ) {
        final Authentication a = SecurityContextHolder.getContext().getAuthentication();

        if ( isAuthorized( a, User.MANAGER ) ) {
            return "managerhome";
        }
        if ( isAuthorized( a, User.STAFF ) ) {
            return "staffhome";
        }
        if ( isAuthorized( a, User.CUSTOMER ) ) {
            return "customerhome";
        }
        return "guesthome";
    }

    /**
     * On a GET request to /recipe, the RecipeController will return
     * /src/main/resources/templates/recipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/recipe", "/recipe.html" } )
    public String addRecipePage ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.STAFF ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }
        return "recipe";
    }

    /**
     * On a GET request to /deleterecipe, the DeleteRecipeController will return
     * /src/main/resources/templates/deleterecipe.html..
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/deleterecipe", "/deleterecipe.html" } )
    public String deleteRecipeForm ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.STAFF ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }
        return "deleterecipe";
    }

    /**
     * On a GET request to /editrecipe, the EditRecipeController will return
     * /src/main/resources/templates/editrecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/editrecipe", "/editrecipe.html" } )
    public String editRecipeForm ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.STAFF ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }
        return "editrecipe";
    }

    /**
     * Handles a GET request for inventory. The GET request provides a view to
     * the client that includes the list of the current ingredients in the
     * inventory and a form where the client can enter more ingredients to add
     * to the inventory.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/inventory", "/inventory.html" } )
    public String inventoryForm ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }
        return "inventory";
    }

    /**
     * On a GET request to /makecoffee, the MakeCoffeeController will return
     * /src/main/resources/templates/makecoffee.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/fulfillorder", "/fulfillorder.html" } )
    public String makeCoffeeForm ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.STAFF ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }
        return "fulfillorder";
    }

    /**
     * On a GET request to /addingredient, the MakeCoffeeController will return
     * /src/main/resources/templates/addingredient.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/addingredient", "/addingredient.html" } )
    public String addIngredientForm ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.STAFF ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }
        return "addingredient";
    }

    /**
     * On a GET request to /addrecipe, the MakeCoffeeController will return
     * /src/main/resources/templates/addrecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/addrecipe", "/addrecipe.html" } )
    public String addRecipeForm ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.STAFF ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }
        return "addrecipe";
    }

    @GetMapping ( { "/login", "/login.html" } )
    public String loginForm ( final Model model ) {
        return "login";
    }

    /**
     * On a GET request to /managereditstaff, the MakeCoffeeController will
     * return /src/main/resources/templates/managereditstaff.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/managereditstaff", "/managereditstaff.html" } )
    public String managerEditStaff ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }
        return "managereditstaff";
    }

    /**
     * On a GET request to /managereditstaff, the MakeCoffeeController will
     * return /src/main/resources/templates/managereditstaff.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/addnewstaff", "/addnewstaff.html" } )
    public String addNewStaff ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }
        return "addnewstaff";
    }

    @GetMapping ( { "/managerhome", "/managerhome.html" } )
    public String managerHome ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }
        return "managerhome";
    }

    static boolean isAuthorized ( final Authentication a, final String role ) {
        return a != null && a.getAuthorities().stream().anyMatch( ( auth ) -> auth.getAuthority().equals( role ) );
    }

    @GetMapping ( { "/guesthome", "/guesthome.html" } )
    public String guestHome ( final Model model ) {
        return "guesthome";
    }

    @GetMapping ( { "/customerhome", "/customerhome.html" } )
    public String customerHome ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.CUSTOMER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }
        return "customerhome";
    }

    @GetMapping ( { "/register", "/register.html" } )
    public String register ( final Model model ) {
        return "register";
    }

    @GetMapping ( { "/staffhome", "/staffhome.html" } )
    public String staffHome ( final Model model ) {
        return "staffhome";
    }

    @GetMapping ( { "order", "order.html" } )
    public String order ( final Model model ) {
        return "order";
    }

    @GetMapping ( { "/orderstatus", "/orderstatus.html" } )
    public String orderstatus ( final Model model, @RequestParam ( required = false ) final String orderNumber ) {
        if ( orderNumber != null ) {
            model.addAttribute( orderNumber );
        }
        return "orderstatus";
    }

    @GetMapping ( { "/menu", "/menu.html" } )
    public String menuPage ( final Model model ) {
        return "menu";
    }

    @GetMapping ( { "/orderhistory", "/orderhistory.html" } )
    public String orderHistory ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.STAFF ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }

        return "orderhistory";
    }

    @GetMapping ( { "/accountinfo", "/accountinfo.html" } )
    public String accountinfo ( final Model model ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.CUSTOMER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }

        return "accountinfo";
    }

}
