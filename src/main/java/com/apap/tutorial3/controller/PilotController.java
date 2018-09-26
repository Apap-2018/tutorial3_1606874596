package com.apap.tutorial3.controller;

import com.apap.tutorial3.model.PilotModel;
import com.apap.tutorial3.service.PilotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class PilotController {
    @Autowired
    private PilotService pilotService;

    @RequestMapping("/pilot/add")
    public String add(@RequestParam(value = "id", required = true) String id,
                      @RequestParam(value = "licenseNumber", required = true) String licenseNumber,
                      @RequestParam(value = "name", required = true) String name,
                      @RequestParam(value = "flyHour", required = true) Integer flyHour
                      ) {

        PilotModel pilot = new PilotModel(id, licenseNumber, name, flyHour);
        pilotService.addPilot(pilot);
        return "add";
    }

    @RequestMapping("/pilot/view")
    public String view(@RequestParam("licenseNumber") String licenseNumber, Model model){
        PilotModel archive = pilotService.getPilotDetailByLicenseNumber(licenseNumber);

        model.addAttribute("pilot", archive);
        return"view-pilot";
    }

    @RequestMapping(value= {"/pilot/view/license-number","pilot/view/license-number/{licenseNumber}"})
    public String viewPath(@PathVariable Optional<String> licenseNumber, Model model){

        if (licenseNumber.isPresent()){
            PilotModel archive = pilotService.getPilotDetailByLicenseNumber(licenseNumber.get());
            if (archive != null){
                model.addAttribute("pilot", archive);
                return "view-pilot";
            }
            else {
                model.addAttribute("error", "Nomor licenseNumber tidak ditemukan");
                return "error";
            }
        }
        else {
            model.addAttribute("error", "Nomor licenseNumber kosong");
            return "error";
        }
    }

    @RequestMapping("/pilot/viewall")
    public String viewall(Model model){
        List<PilotModel> archive = pilotService.getPilotList();

        model.addAttribute("listPilot", archive);
        return"viewall-pilot";
    }

    @RequestMapping(value= {"/pilot/update/license-number/{licenseNumber}/fly-hour/{flyHour}"})
    public String updateFlyHour(@PathVariable Optional<String> licenseNumber, Model model,
                                @PathVariable Optional<Integer> flyHour){

        if (licenseNumber.isPresent()) {
            PilotModel archive = pilotService.getPilotDetailByLicenseNumber(licenseNumber.get());
            if (archive != null && flyHour.isPresent()){
                archive.setFlyHour(flyHour.get());
                return "update";
            }
            else if (!flyHour.isPresent()){
                model.addAttribute("error", "flyHour tidak ditemukan. Update dibatalkan");
                return "error";
            }
            else {
                model.addAttribute("error", "licenseNumber tidak ditemukan. Update dibatalkan");
                return "error";
            }
        }
        else {
            model.addAttribute("error", "Nomor licenseNumber kosong. Skips.");
            return "error";
        }
    }


    @RequestMapping(value= {"/pilot/delete/id", "/pilot/delete/id/{id}"})
    public String deletePilot(@PathVariable Optional<String> id, Model model) {
        List<PilotModel> pilotList = pilotService.getPilotList();

        if (id.isPresent()) {
            for (int i = 0; i < pilotList.size(); i++) {
                if (pilotList.get(i).getId().equals(id.get())) {
                    pilotList.remove(i);
                    return "delete";
                } else {
                    model.addAttribute("error", "id tidak ditemukan. Delete dibatalkan");
                    return "error";
                }
            }
        } else {
            model.addAttribute("error", "id kosong. Skips.");
            return "error";
        }
        model.addAttribute("error", "id tidak ditemukan. Delete dibatalkan");
        return "error";
    }
}
