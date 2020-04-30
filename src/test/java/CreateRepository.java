import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.fanpu.common.util.FileUtil;

import java.io.File;

public class CreateRepository {
    static final Logger logger = LoggerFactory.getLogger(CreateRepository.class);

    public static void main(String[] args) throws Exception {
        File directory = new File("");//设定为当前文件夹
        String url = directory.getAbsolutePath() + "/src/main/java/tech/fanpu/";
        File[] pos = new File(url + "po").listFiles();
        Boolean createBusiness = true;
        String name = null;
        for (File po : pos) {
            if (po.getName().indexOf(".java") != -1) {
                name = po.getName().replace(".java", "");
                if (name.equals("BasePo")) {
                    continue;
                }
                if (new File(url + "repository/" + name + "Repository.java").exists() == false) {
                    logger.info("{} 不存在，正在创建", name);
                    String content = "package tech.fanpu.repository; \n\nimport org.springframework.data.jpa.repository.JpaRepository;\n"
                        + "import tech.fanpu.po.%s; "
                        + "\n\npublic interface %sRepository extends JpaRepository<%s, Long>{\n\n}";

                    FileUtil.writeText(url + "repository/" + name + "Repository.java", String.format(content, name, name, name));

                    if (!createBusiness) {
                        continue;
                    }

                    content = "package tech.fanpu.business;\n" +
                        "\n" +
                        "import tech.fanpu.po.%s;\n" +
                        "\n" +
                        "\n" +
                        "public interface %sBusiness extends BaseBusiness<%s>{\n" +
                        "}\n";

                    FileUtil.writeText(url + "business/" + name + "Business.java", String.format(content, name, name, name));

                    content = "package tech.fanpu.business.impl;\n" +
                        "\n" +
                        "import lombok.extern.log4j.Log4j2;\n" +
                        "import org.springframework.stereotype.Service;\n" +
                        "import tech.fanpu.business.%sBusiness;\n" +
                        "import tech.fanpu.po.%s;\n" +
                        "import javax.transaction.Transactional;\n" +
                        "\n" +
                        "@Log4j2\n" +
                        "@Service\n" +
                        "@Transactional\n" +
                        "public class %sBusinessImpl extends BaseBusinessImpl<%s> implements %sBusiness{\n" +
                        "\n" +
                        "}\n";
                    FileUtil.writeText(url + "business/impl/" + name + "BusinessImpl.java", String.format(content, name, name, name, name, name));

                    content = "package tech.fanpu.controller.web;\n" +
                        "\n" +
                        "import org.springframework.beans.factory.annotation.Autowired;\n" +
                        "import org.springframework.stereotype.Controller;\n" +
                        "import org.springframework.web.bind.annotation.*;\n" +
                        "import org.springframework.web.servlet.ModelAndView;\n" +
                        "import tech.fanpu.bo.GridBo;\n" +
                        "import tech.fanpu.business.%sBusiness;\n" +
                        "import tech.fanpu.common.util.response.ResultBean;\n" +
                        "import tech.fanpu.po.%s;\n" +
                        "import tech.fanpu.vo.GridParamsVo;\n" +
                        "import javax.validation.Valid;\n" +
                        "import java.util.HashMap;\n" +
                        "import java.util.Map;\n" +
                        "\n" +
                        "\n" +
                        "@Controller\n" +
                        "@RequestMapping(\"/web/#lowercaseName#\")\n" +
                        "public class %sController{\n" +
                        "  @Autowired\n" +
                        "  %sBusiness #lowercaseName#Business;\n" +
                        "\n" +
                        "  @GetMapping\n" +
                        "  public ModelAndView index() {\n" +
                        "    return new ModelAndView(\"web/#lowercaseName#/#lowercaseName#\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @PostMapping\n" +
                        "  public ModelAndView save(@Valid %s #lowercaseName#) {\n" +
                        "    #lowercaseName#Business.saveOrUpdate(#lowercaseName#, #lowercaseName#.get%sId());\n" +
                        "    return new ModelAndView(\"redirect:/web/#lowercaseName#\");\n" +
                        "  }\n" +
                        "\n" +
                        "  @GetMapping(\"new\")\n" +
                        "  public ModelAndView newPage() {\n" +
                        "    Map<String, Object> params = new HashMap<>();\n" +
                        "    params.put(\"#lowercaseName#\", new %s());\n" +
                        "    return new ModelAndView(\"web/#lowercaseName#/newPage\", params);\n" +
                        "  }\n" +
                        "\n" +
                        "  @GetMapping(\"{id}\")\n" +
                        "  public ModelAndView find(@PathVariable(\"id\") Long id) {\n" +
                        "    Map<String, Object> params = new HashMap<>();\n" +
                        "    params.put(\"#lowercaseName#\", #lowercaseName#Business.findById(id));\n" +
                        "    return new ModelAndView(\"web/#lowercaseName#/newPage\", params);\n" +
                        "  }\n" +
                        "\n" +
                        "  @ResponseBody\n" +
                        "  @DeleteMapping\n" +
                        "  public ResultBean del(@RequestParam Long id) {\n" +
                        "    #lowercaseName#Business.delete(id);\n" +
                        "    return ResultBean.defaultSuccess();\n" +
                        "  }\n" +
                        "\n" +
                        "  @ResponseBody\n" +
                        "  @PostMapping(\"grid\")\n" +
                        "  public GridBo list(GridParamsVo params) {\n" +
                        "    return #lowercaseName#Business.list(params, \"name\");\n" +
                        "  }\n" +
                        "}\n";
                    content = content.replaceAll("%s", name);
                    String tempName = name.substring(0, 1).toLowerCase() + name.substring(1);
                    content = content.replaceAll("#lowercaseName#", tempName);
                    FileUtil.writeText(url + "controller/web/" + name + "Controller.java", content);
                    logger.info("{} 创建成功...", name);
                }
            }
        }
    }
}
