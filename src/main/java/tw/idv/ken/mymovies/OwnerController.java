package tw.idv.ken.mymovies;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import tw.idv.ken.mymovies.model.Owner;

@RooWebJson(jsonObject = Owner.class)
@Controller
@RequestMapping("/owners")
public class OwnerController {
}
