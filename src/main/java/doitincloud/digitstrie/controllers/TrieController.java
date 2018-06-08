package doitincloud.digitstrie.controllers;

import doitincloud.commons.Utils;
import doitincloud.digitstrie.services.DigitsTrieServices;
import doitincloud.rdbcache.configs.PropCfg;
import doitincloud.rdbcache.exceptions.BadRequestException;
import doitincloud.rdbcache.exceptions.NotFoundException;
import doitincloud.rdbcache.supports.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class TrieController {

    @Value("${default.table:dt_number_info}")
    private String defaultTable;

    @Autowired
    DigitsTrieServices digitsTrieServices;

    @RequestMapping(value = {
            "/v1/get/{value}",
            "/v1/get/{value}/{opt}"
    }, method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> get(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt) {
        TableNumber tn = prepare(request, value, opt);
        Map<String, Object> map = digitsTrieServices.get(tn.context, tn.table, tn.number);
        if (map != null && map.size() > 0) {
            Utils.getExcutorService().submit(() -> tn.context.closeMonitor());
            return map;
        }
        throw new NotFoundException("Not found from " + tn.table + " for " + tn.number);
    }

    @RequestMapping(value = {
            "/v1/start-with/{value}",
            "/v1/start-with/{value}/{opt}"
    }, method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> allStartWith(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt) {
        TableNumber tn = prepare(request, value, opt);
        Map<String, Object> map = digitsTrieServices.allStartWith(tn.context, tn.table, tn.number);
        if (map != null && map.size() > 0) {
            Utils.getExcutorService().submit(() -> tn.context.closeMonitor());
            return map;
        }
        throw new NotFoundException("Not found from " + tn.table + " for " + tn.number);
    }

    @RequestMapping(value = {
            "/v1/longest-match/{value}",
            "/v1/longest-match/{value}/{opt}"
    }, method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> longestMatch(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt) {
        TableNumber tn = prepare(request, value, opt);
        Map<String, Object> map = digitsTrieServices.longestMatch(tn.context, tn.table, tn.number);
        if (map != null && map.size() > 0) {
            Utils.getExcutorService().submit(() -> tn.context.closeMonitor());
            return map;
        }
        throw new NotFoundException("Not found from " + tn.table + " for " + tn.number);
    }

    @RequestMapping(value = {
            "/v1/save/{value}",
            "/v1/save/{value}/{opt}"
    }, method = {RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT})
    @ResponseBody
    public Map<String, Object> save(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt,
            @RequestBody Map<String, Object> map) {
        TableNumber tn = prepare(request, value, opt);
        Map<String, Object> mapReturn = digitsTrieServices.save(tn.context, tn.table, tn.number, map);
        Utils.getExcutorService().submit(() -> tn.context.closeMonitor());
        return mapReturn;
    }

    @RequestMapping(value = {
            "/v1/delete/{value}",
            "/v1/delete/{value}/{opt}"
    }, method = {RequestMethod.DELETE, RequestMethod.GET})
    @ResponseBody
    public Map<String, Object> delete(
            HttpServletRequest request,
            @PathVariable("value") String value,
            @PathVariable("opt") Optional<String> opt) {
        TableNumber tn = prepare(request, value, opt);
        boolean result = digitsTrieServices.delete(tn.context, tn.table, tn.number);
        if (result) {
            Utils.getExcutorService().submit(() -> tn.context.closeMonitor());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("timestamp", new Date());
            map.put("status", 200);
            map.put("message", "OK");
            return map;
        }
        throw new NotFoundException("Not found from " + tn.table + " for " + tn.number);
    }

    private TableNumber prepare(HttpServletRequest request, String value, Optional<String> opt) {

        TableNumber tn = new TableNumber();

        String rawNumber= null;
        if (opt.isPresent()) {
            if (Character.isLetter(value.charAt(0))) {
                tn.table = value;
                rawNumber = opt.get();
            } else {
                rawNumber = value;
                tn.table = opt.get();
            }
        } else {
            rawNumber = value;
        }
        if (!digitsTrieServices.hasTable(tn.table)) {
            throw new BadRequestException("invalid table");
        }
        StringBuffer numberBuffer = new StringBuffer();
        for (int i = 0; i < rawNumber.length(); i++) {
            char c = rawNumber.charAt(i);
            if (c < '0' || c > '9' ) {
                continue;
            }
            numberBuffer.append(c);
        }
        if (numberBuffer.length() == 0) {
            throw new BadRequestException("invalid number");
        }
        tn.number =  numberBuffer.toString();

        tn.context = new Context();
        if (PropCfg.getEnableMonitor()) tn.context.enableMonitor(request);

        return tn;
    }

    class TableNumber {
        String table = defaultTable;
        String number;
        Context context;
    }
}

