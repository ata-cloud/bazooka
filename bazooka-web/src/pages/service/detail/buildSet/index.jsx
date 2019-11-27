import React from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { connect } from 'dva';
import { Card, Modal, Form, Button, Steps, message } from 'antd';
import { service } from '@/services/service';
import BaseInfo from './BaseInfo';
import Compile from './Compile';
import Container from './Container';
import ExtarSet from './ExtarSet';
const FormItem = Form.Item;
const { Step } = Steps;
class BuildSet extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      step: 0,
      data: {},
      steps: [],
      i: 0
    };
  }
  componentDidMount() {
    const { deployMode, currentItem } = this.props;
    if (deployMode == 'BUILD') {
      this.setState({
        steps: [0, 1, 2, 3]
      })
    } else {
      this.setState({
        steps: [0, 2, 3]
      })
    }
    if (currentItem.id) {
      this.onFetchDetail();
    }
  }
  onFetchDetail = async () => {
    const { currentItem } = this.props;
    let res = await service.appDeployConfigGet({ configId: currentItem.id });
    if (res && res.code == '1') {
      let detail = res.data || {}, environmentVariable = [], keys = [], keys1 = [], keys2 = [], keys3 = [];
      detail.portMappings && detail.portMappings.map((vo, i) => {
        keys.push(i)
      })

      if (detail.environmentVariable) {
        Object.keys(detail.environmentVariable).map((item, i) => {
          keys1.push(i);
          environmentVariable.push({
            key: item,
            value: detail.environmentVariable[item]
          })
        })
      }

      detail.volumes && detail.volumes.map((vo, i) => {
        keys2.push(i)
      })

      detail.healthChecks && detail.healthChecks.map((vo, i) => {
        keys3.push(i)
      })
      this.setState({
        data: {
          ...detail,
          keys,
          keys1,
          keys2,
          keys3,
          environmentVariable,
          branch: detail.gitBranchAllow ? '2' : detail.gitBranchDeny ? '3' : '1',
          gitBranch: detail.gitBranchAllow || detail.gitBranchDeny
        }
      })
    }
  }
  onSubmit = (e, type) => {
    const { step, data, steps, i } = this.state;
    e && e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        let portMappings = values.portMappings || data.portMappings;
        let branch = values.branch || data.branch, gitBranch = values.gitBranch || data.gitBranch;
        let gitBranchs = branch == '2' ? {
          gitBranchAllow: gitBranch,
          gitBranchDeny: undefined
        } : branch == '3' ? {
          gitBranchAllow: undefined,
          gitBranchDeny: gitBranch
        } : {
              gitBranchAllow: undefined,
              gitBranchDeny: undefined
            }
        this.setState({
          data: {
            ...data,
            ...values,
            portMappings: portMappings && portMappings.filter(item => item.containerPort),
            ...gitBranchs
          },
        }, () => {
          if (type == 'next') {
            this.setState({
              i: i + 1,
              step: steps[i + 1]
            })
            return
          }
          this.onSave()
        });
      }
    });
  }
  onNext = () => {
    const { step } = this.state;
    this.onSubmit('', 'next')
    // this.setState({
    //   step: step + 1
    // })
  }
  onSave = async () => {
    const { data } = this.state;
    const { info, deployMode, onCancel, onSavaSuccess, currentItem, currentEnvO } = this.props;
    let params = {}, environmentVariable = {}, volumes = [];
    if (data.environmentVariable && data.environmentVariable.length) {
      data.environmentVariable.map((item) => {
        environmentVariable[item.key] = item.value
      })
    }
    volumes = data.volumes && data.volumes.filter(item => item.hostPath);
    params = {
      ...data,
      environmentVariable: environmentVariable,
      volumes: volumes,
      appId: info.appId,
      envId: info.envId,
      deployMode: deployMode,
      instance: currentEnvO.clusterType !== '2' ? data.instance : data.clusterNodes.length
    }
    if (currentItem.type && currentItem.type == 'edit') {
      let res = await service.appDeployConfigUpdate({ ...params, configId: currentItem.id });
      if (res && res.code == '1') {
        message.success('修改成功');
        onCancel();
        onSavaSuccess();
      }
    } else {
      let res = await service.appDeployConfigCreate({ ...params, id: null });
      if (res && res.code == '1') {
        message.success('创建成功');
        onCancel();
        onSavaSuccess();
      }
    }

  }
  onPre = () => {
    const { step, i, steps } = this.state;
    this.setState({
      i: i - 1,
      step: steps[i - 1]
    })
  }
  render() {
    const { visible, form, onCancel, deployMode, info, currentItem } = this.props;
    const { step, data, i } = this.state;
    let deployModeName = deployMode === 'BUILD' ? '构建发布' : '镜像发布';
    return (
      <Modal
        title={currentItem.type == 'edit' ? `修改发布配置 - ${deployModeName}` : `新建发布配置 - ${deployModeName}`}
        width={1000}
        visible={visible}
        onCancel={onCancel}
        footer={
          <div>
            {
              step > 0 && <Button onClick={this.onPre}>上一步</Button>
            }
            {
              step < 3 && <Button onClick={this.onNext}>下一步</Button>
            }
            {
              step > 1 && <Button onClick={() => this.onSubmit('', 'save')} type="primary">保存</Button>
            }
          </div>
        }
      >
        <div style={{ padding: '0 60px' }}>
          <Steps current={i} style={{ padding: '0 100px' }} size="small" >
            <Step title="基本信息" />
            {
              deployMode.indexOf('BUILD') > -1 == 'BUILD' && <Step title="编译构建" />
            }
            <Step title="容器运行" />
            <Step title="额外设置" />
          </Steps>
          <Form onSubmit={this.onSubmit}>
            {step === 0 && <BaseInfo formParent={form} data={data} deployMode={deployMode} currentItem={currentItem} />}
            {step === 1 && deployMode.indexOf('BUILD') > -1 && <Compile formParent={form} data={data} />}
            {step === 2 && <Container formParent={form} data={data} info={info} />}
            {step === 3 && <ExtarSet formParent={form} data={data} />}
          </Form>
        </div>
      </Modal>
    );
  }
}
export default Form.create()(connect(({ service }) => ({
  currentEnvO: service.currentEnvO,
}))(BuildSet));

